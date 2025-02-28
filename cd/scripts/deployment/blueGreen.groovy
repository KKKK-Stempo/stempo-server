// scripts/deployment/blueGreen.groovy
/**
 * Blue-Green 배포 관련 함수들
 */

/**
 * 컨테이너를 결정하는 함수.
 *
 * @param blueContainer  : Blue 컨테이너 이름 (String)
 * @param greenContainer : Green 컨테이너 이름 (String)
 * @param blueUrl        : Blue 컨테이너 URL (String)
 * @param greenUrl       : Green 컨테이너 URL (String)
 * @param portA          : Blue 포트 (String)
 * @param portB          : Green 포트 (String)
 * @param targetMap      : 결과를 저장할 Map (예: vars)
 */
def determineContainers(Map params = [:]) {
    if (!params.blueContainer || !params.greenContainer || !params.blueUrl || !params.greenUrl ||
        !params.portA || !params.portB || !params.targetMap) {
        error "determineContainers: 필요한 파라미터가 누락되었습니다."
    }
    def blueRunning = sh(script: "docker ps --filter 'name=${params.blueContainer}' --format '{{.Names}}' | grep -q '${params.blueContainer}'", returnStatus: true) == 0
    if (blueRunning) {
        params.targetMap.CURRENT_CONTAINER = params.blueContainer
        params.targetMap.DEPLOY_CONTAINER = params.greenContainer
        params.targetMap.NEW_TARGET = params.greenUrl
        params.targetMap.NEW_PORT = params.portB
        params.targetMap.OLD_PORT = params.portA
    } else {
        params.targetMap.CURRENT_CONTAINER = params.greenContainer
        params.targetMap.DEPLOY_CONTAINER = params.blueContainer
        params.targetMap.NEW_TARGET = params.blueUrl
        params.targetMap.NEW_PORT = params.portA
        params.targetMap.OLD_PORT = params.portB
    }
    echo "Current container: ${params.targetMap.CURRENT_CONTAINER}, deploying to ${params.targetMap.DEPLOY_CONTAINER} on port ${params.targetMap.NEW_PORT}."
}

/**
 * 트래픽 전환 및 정리 함수.
 *
 * 필수:
 *   nginxContainer  : Nginx 컨테이너 이름 (String)
 *   newTarget       : 새로운 타겟 URL (String)
 *   nginxConfigPath : Nginx 설정 파일 경로 (String)
 *   oldPort         : 기존 포트 (String)
 *   newPort         : 변경 후 포트 (String)
 *   currentContainer: 기존 컨테이너 이름 (String)
 */
def switchTrafficAndCleanup(Map params = [:]) {
    if (!params.nginxContainer || !params.newTarget || !params.nginxConfigPath ||
        !params.oldPort || !params.newPort || !params.currentContainer) {
        error "switchTrafficAndCleanup: 필요한 파라미터가 누락되었습니다."
    }
    echo "Switching traffic to new target on port ${params.newPort}."

    // Nginx 관련 함수 호출 (공통 파일)
    def nginxUtil = load 'scripts/common/nginx.groovy'
    nginxUtil.updateConfig(
        container: params.nginxContainer,
        targetUrl: params.newTarget,
        configFile: params.nginxConfigPath,
        oldPort: params.oldPort,
        newPort: params.newPort
    )
    nginxUtil.reloadConfig(container: params.nginxContainer)

    // 기존 컨테이너 중지 및 제거
    def current = params.currentContainer
    def isRunning = sh(script: "docker ps --filter 'name=${current}' --format '{{.Names}}' | grep -q '${current}'", returnStatus: true) == 0
    if (isRunning) {
        sh """
            docker stop ${current}
            docker rm ${current}
            echo "Stopped and removed ${current}."
        """
    } else {
        echo "No running container ${current} found."
    }
}

return this
