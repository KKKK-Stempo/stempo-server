// cd/scripts/common/healthCheck.groovy
/**
 * 헬스체크를 수행하는 함수.
 *
 * 반드시 healthCheckUrl 파라미터를 전달받아 해당 URL로 헬스체크 요청을 보냅니다.
 * whitelistUsername과 whitelistPassword가 제공되면 Basic Auth 인증 옵션을 추가하여 요청합니다.
 *
 * @param healthCheckUrl : 헬스체크 요청 URL (예: "http://example.com:8080/actuator/health") - 필수
 * @param whitelistUsername : 인증 아이디 (선택, 없으면 인증 없이 요청)
 * @param whitelistPassword : 인증 비밀번호 (선택, 없으면 인증 없이 요청)
 * @param timeout : 타임아웃 시간 (ms, 기본 150000)
 * @param deployContainer : 헬스체크 실패 시 중지할 컨테이너 이름 (선택)
 */
def performHealthCheck(Map params = [:]) {
    if (!params.healthCheckUrl) {
        error "performHealthCheck: healthCheckUrl 파라미터는 필수입니다."
    }

    echo "Health Check URL: ${params.healthCheckUrl}"

    def start_time = System.currentTimeMillis()
    def TIMEOUT_MS = params.timeout ?: 150000
    def timeout = start_time + TIMEOUT_MS

    while (System.currentTimeMillis() < timeout) {
        def elapsed = (System.currentTimeMillis() - start_time) / 1000
        echo "Health check... ${elapsed} sec elapsed."

        // 기본 curl 명령어 구성
        def curlCmd = "curl -s"
        if (params.whitelistUsername && params.whitelistPassword) {
            curlCmd += " -u ${params.whitelistUsername}:${params.whitelistPassword}"
        }
        curlCmd += " ${params.healthCheckUrl} | grep 'UP'"

        def status = sh(script: curlCmd, returnStatus: true)
        if (status == 0) {
            echo "Application is UP after ${elapsed} seconds."
            return
        }
        sleep 5
    }

    if (params.deployContainer) {
        sh "docker stop ${params.deployContainer}"
        sh "docker rm ${params.deployContainer}"
    }
    error "Health check failed."
}

return this
