// scripts/deployment/rolling.groovy
/**
 * Rolling 배포 관련 함수들
 */

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
