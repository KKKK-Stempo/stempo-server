// scripts/deployment/rolling.groovy
/**
 * Rolling 배포 관련 함수들
 */

/**
 * 도커 이미지 빌드 및 푸시 함수 (Rolling 배포용)
 *
 * @param dockerfilePath : Dockerfile 경로 (String)
 * @param imageName : 이미지 이름 (String)
 * @param containerName : 컨테이너 이름 (String)
 * @param dockerHubRepo : Docker Hub Repository (String)
 */
def buildAndPushDockerImage(Map params = [:]) {
    if (!params.dockerfilePath || !params.imageName || !params.containerName || !params.dockerHubRepo) {
        error "buildAndPushDockerImage (rolling): 필요한 파라미터가 누락되었습니다."
    }
    sh """
        DOCKER_BUILDKIT=1 docker build -f ${params.dockerfilePath} -t ${params.imageName}:${params.containerName} .
        docker tag ${params.imageName}:${params.containerName} ${params.dockerHubRepo}:${params.containerName}
        docker push ${params.dockerHubRepo}:${params.containerName}
    """
}

/**
 * 새 인스턴스를 배포하는 함수 (Rolling 배포용)
 *
 * @param containerName : 컨테이너 이름 (String)
 * @param appPort : 어플리케이션 포트 (String)
 * @param applicationNetwork : 애플리케이션 네트워크 (String)
 * @param imageName : 이미지 이름 (String)
 */
def deployNewInstance(Map params = [:]) {
    if (!params.containerName || !params.appPort || !params.applicationNetwork || !params.imageName) {
        error "deployNewInstance (rolling): 필요한 파라미터가 누락되었습니다."
    }
    sh """
        echo "Stopping/removing ${params.containerName} if exists"
        if docker ps | grep -q ${params.containerName}; then
            docker stop ${params.containerName}
            docker rm ${params.containerName}
        fi
        echo "Running new container ${params.containerName}"
        docker run -d --name ${params.containerName} \\
            -p ${params.appPort}:5000 \\
            --network ${params.applicationNetwork} \\
            --restart always \\
            ${params.imageName}:${params.containerName}
            
        if docker network ls --format '{{.Name}}' | grep -q '^${params.infraNetwork}\$'; then
            docker network connect ${params.infraNetwork} ${params.containerName}
        else
            echo "Infra network ${params.infraNetwork} not found. Skipping."
        fi
        docker ps -a
    """
}

return this
