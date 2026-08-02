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

    archiveArtifacts(
        artifacts: 'zap-report.*',
        fingerprint: true,
        allowEmptyArchive: true
    )

    switch (exitCode) {
        case 0:
            echo "No FAIL alerts."
            break

        case 1:
            error("FAIL-level vulnerabilities found.")
            break

        case 2:
            unstable("WARN-level vulnerabilities found.")
            break

        default:
            error("ZAP execution failed.")
        }
}