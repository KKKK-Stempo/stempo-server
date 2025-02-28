// cd/scripts/deployment/blueGreen.groovy
/**
 * Blue-Green 배포 관련 함수들
 */

/**
 * 컨테이너를 결정하는 함수.
 *
 * @param blueContainer : Blue 컨테이너 이름 (String)
 * @param greenContainer : Green 컨테이너 이름 (String)
 * @param blueUrl : Blue 컨테이너 URL (String)
 * @param greenUrl : Green 컨테이너 URL (String)
 * @param portA : Blue 포트 (String)
 * @param portB : Green 포트 (String)
 * @param targetMap : 결과를 저장할 Map (예: vars)
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

return this
