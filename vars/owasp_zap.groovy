def call(String targetUrl) {

    int exitCode = sh(
        script: """
            docker run --rm \
                --user root \
                --network host \
                -v \$(pwd):/zap/wrk/:rw \
                ghcr.io/zaproxy/zaproxy:stable \
                zap-baseline.py \
                -t ${targetUrl} \
                -r zap-report.html \
                -J zap-report.json \
                -x zap-report.xml
        """,
        returnStatus: true
    )

    echo "ZAP Exit Code: ${exitCode}"

    if (fileExists('zap-report.html')) {
        publishHTML(target: [
            reportDir: '.',
            reportFiles: 'zap-report.html',
            reportName: 'OWASP ZAP Report',
            keepAll: true,
            alwaysLinkToLastBuild: true,
            allowMissing: false
        ])
    } else {
        echo "ZAP HTML report not found."
    }

    archiveArtifacts artifacts: 'zap-report.*', fingerprint: true, allowEmptyArchive: true

    switch (exitCode) {
        case 0:
            echo "OWASP ZAP scan passed."
            break

        case 1:
            unstable("OWASP ZAP found warning(s). Check the HTML report.")
            break

        case 2:
            error("OWASP ZAP found high-risk vulnerability(ies).")
            break

        default:
            error("OWASP ZAP failed with exit code ${exitCode}.")
    }
}