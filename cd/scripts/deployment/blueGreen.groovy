// scripts/deployment/blueGreen.groovy
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
    if (!params.blueContainer || !params.greenContainer || !params.blueUrl || !params.greenUrl || !params.portA || !params.portB || !params.targetMap) {
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
 * 도커 이미지 빌드 및 푸시 함수.
 *
 * @param dockerfilePath : Dockerfile 경로 (String)
 * @param imageName : 이미지 이름 (String)
 * @param deployContainer : 배포할 컨테이너 이름 (String)
 * @param dockerHubRepo : Docker Hub Repository (String)
 */
def buildAndPushDockerImage(Map params = [:]) {
    if (!params.dockerfilePath || !params.imageName || !params.deployContainer || !params.dockerHubRepo) {
        error "buildAndPushDockerImage: 필요한 파라미터가 누락되었습니다."
    }
    sh """
        DOCKER_BUILDKIT=1 docker build -f ${params.dockerfilePath} -t ${params.imageName}:${params.deployContainer} .
        docker tag ${params.imageName}:${params.deployContainer} ${params.dockerHubRepo}:${params.deployContainer}
        docker push ${params.dockerHubRepo}:${params.deployContainer}
    """
}

/**
 * 새 인스턴스를 배포하는 함수.
 *
 * @param deployContainer : 새 컨테이너 이름 (String)
 * @param newPort : 새 포트 (String)
 * @param applicationNetwork : 애플리케이션 네트워크 (String)
 * @param hostDefaultPath : 호스트 기본 경로 (String)
 * @param containerDefaultPath : 컨테이너 기본 경로 (String)
 * @param hostLogsPath : 호스트 로그 경로 (String)
 * @param containerLogsPath : 컨테이너 로그 경로 (String)
 * @param profile : Spring 프로파일 (String)
 * @param imageName : 이미지 이름 (String)
 */
def deployNewInstance(Map params = [:]) {
    if (!params.deployContainer || !params.newPort || !params.applicationNetwork ||
        !params.hostDefaultPath || !params.containerDefaultPath || !params.hostLogsPath ||
        !params.containerLogsPath || !params.profile || !params.imageName) {
        error "deployNewInstance: 필요한 파라미터가 누락되었습니다."
    }
    sh """
        echo "Stopping/removing container ${params.deployContainer} if exists"
        if docker ps | grep -q ${params.deployContainer}; then
            docker stop ${params.deployContainer}
            docker rm ${params.deployContainer}
        fi
        echo "Running new container ${params.deployContainer}"
        docker run -d --name ${params.deployContainer} \\
            -p ${params.newPort}:8080 \\
            --network ${params.applicationNetwork} \\
            -v ${params.hostDefaultPath}:${params.containerDefaultPath} \\
            -v ${params.hostLogsPath}:${params.containerLogsPath} \\
            -e LOG_PATH=${params.containerLogsPath} \\
            -e SPRING_PROFILES_ACTIVE=${params.profile} \\
            --restart unless-stopped \\
            ${params.imageName}:${params.deployContainer}
            
        if docker network ls --format '{{.Name}}' | grep -q '^${params.infraNetwork}\$'; then
            docker network connect ${params.infraNetwork} ${params.deployContainer}
        else
            echo "Infra network ${params.infraNetwork} not found. Skipping."
        fi
        docker ps -a
    """
}

/**
 * 트래픽 전환 및 정리 함수.
 *
 * @param nginxContainer : Nginx 컨테이너 이름 (String)
 * @param newTarget : 새로운 타겟 URL (String)
 * @param nginxConfigPath : Nginx 설정 파일 경로 (String)
 * @param oldPort : 기존 포트 (String)
 * @param newPort : 변경 후 포트 (String)
 * @param currentContainer : 기존 컨테이너 이름 (String)
 */
def switchTrafficAndCleanup(Map params = [:]) {
    if (!params.nginxContainer || !params.newTarget || !params.nginxConfigPath || !params.oldPort || !params.newPort || !params.currentContainer) {
        error "switchTrafficAndCleanup: 필요한 파라미터가 누락되었습니다."
    }
    echo "Switching traffic to new target on port ${params.newPort}."
    updateNginxConfig(params.nginxContainer, params.newTarget, params.nginxConfigPath, params.oldPort, params.newPort)
    reloadNginx(params.nginxContainer)
    stopAndRemoveContainer(params.currentContainer)
}

def updateNginxConfig(String nginxContainer, String newTargetUrl, String configPath, String oldPort, String newPort) {
    sh """
        docker exec ${nginxContainer} bash -c '
            export TARGET_URL=${newTargetUrl}
            envsubst "\\\$TARGET_URL" < ${configPath}.template > ${configPath}
        '
        docker exec ${nginxContainer} sed -i 's/${oldPort}/${newPort}/' ${configPath}
    """
}

def reloadNginx(String nginxContainer) {
    sh """
        docker exec ${nginxContainer} nginx -t
        docker exec ${nginxContainer} nginx -s reload
        echo "Nginx reloaded."
    """
}

def stopAndRemoveContainer(String containerName) {
    def containerRunning = sh(script: "docker ps --filter 'name=${containerName}' --format '{{.Names}}' | grep -q '${containerName}'", returnStatus: true) == 0
    if (containerRunning) {
        sh """
            docker stop ${containerName}
            docker rm ${containerName}
            echo "Stopped and removed ${containerName}."
        """
    } else {
        echo "No running container ${containerName} found."
    }
}

return this
