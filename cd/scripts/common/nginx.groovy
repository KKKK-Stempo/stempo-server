// scripts/common/nginx.groovy
/**
 * Nginx 설정 파일 업데이트 함수.
 *
 * @param container : Nginx 컨테이너 이름 (String)
 * @param targetUrl : 대체할 새로운 타겟 URL (String)
 * @param configFile: Nginx 설정 파일 경로 (String)
 * @param oldPort   : 기존 포트 (String)
 * @param newPort   : 변경 후 포트 (String)
 */
def updateConfig(Map params = [:]) {
    if (!params.container || !params.targetUrl || !params.configFile || !params.oldPort || !params.newPort) {
        error "updateConfig: container, targetUrl, configFile, oldPort, newPort 파라미터가 필요합니다."
    }
    sh """
        docker exec ${params.container} bash -c '
            export TARGET_URL=${params.targetUrl}
            envsubst "\\\$TARGET_URL" < ${params.configFile}.template > ${params.configFile}
        '
        docker exec ${params.container} sed -i 's/${params.oldPort}/${params.newPort}/' ${params.configFile}
    """
}

/**
 * Nginx 설정을 재로딩하는 함수.
 *
 * @param container: Nginx 컨테이너 이름 (String)
 */
def reloadConfig(Map params = [:]) {
    if (!params.container) {
        error "reloadConfig: container 파라미터가 필요합니다."
    }
    sh """
        docker exec ${params.container} nginx -t
        docker exec ${params.container} nginx -s reload
        echo "Nginx configuration reloaded."
    """
}

return this
