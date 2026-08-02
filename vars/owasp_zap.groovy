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
            echo "OWASP ZAP scan completed successfully. No alerts found."
            break

        case 1:
            error("OWASP ZAP detected one or more high-risk vulnerabilities.")
            break

        case 2:
            unstable("OWASP ZAP detected warning-level issues. Review the published report.")
            break

        case 3:
            error("OWASP ZAP encountered an internal error during the scan.")
            break

        default:
            error("Unknown OWASP ZAP exit code: ${exitCode}")
    }
}