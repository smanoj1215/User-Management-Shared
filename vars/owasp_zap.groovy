def call(String targetUrl) {

    sh """
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
    """

    publishHTML(target: [
        reportDir: '.',
        reportFiles: 'zap-report.html',
        reportName: 'OWASP ZAP Report',
        keepAll: true,
        alwaysLinkToLastBuild: true,
        allowMissing: false
    ])

    archiveArtifacts artifacts: 'zap-report.*', fingerprint: true
}