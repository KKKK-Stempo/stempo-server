// scripts/common/docker.groovy
/**
 * Docker Hub에 로그인하는 함수.
 *
 * @param credentialsId : Jenkins 크리덴셜 ID (String)
 */
def dockerLogin(Map params = [:]) {
    if (!params.credentialsId) {
        error "dockerLogin: credentialsId 파라미터가 필요합니다."
    }
    withCredentials([usernamePassword(credentialsId: params.credentialsId, usernameVariable: 'DOCKER_HUB_USER', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
        sh """
            echo "Logging in to Docker Hub..."
            echo \$DOCKER_HUB_PASSWORD | docker login -u \$DOCKER_HUB_USER --password-stdin
        """
    }
}

/**
 * 공통 도커 이미지 빌드 및 푸시 함수.
 *
 * 이 함수는 Blue-Green 배포와 Rolling 배포 모두에서 사용할 수 있습니다.
 * 배포 방식에 따라 tag 파라미터에 deployContainer (Blue-Green) 또는 containerName (Rolling)을 전달하면 됩니다.
 *
 * @param dockerfile : Dockerfile 경로 (String)
 * @param image : 이미지 이름 (String)
 * @param tag : 빌드한 이미지에 태그로 사용할 값 (String)
 * @param repo : Docker Hub Repository (String)
 * @param context : 빌드 컨텍스트 (String, 예: "." or "./path")
 */
def buildAndPushDockerImage(Map params = [:]) {
    if (!params.dockerfile || !params.image || !params.tag || !params.repo || !params.context) {
        error "buildAndPushDockerImage: 필요한 파라미터(dockerfile, image, tag, repo, context)가 누락되었습니다."
    }
    sh """
        DOCKER_BUILDKIT=1 docker build -f ${params.dockerfile} -t ${params.image}:${params.tag} ${params.context}
        docker tag ${params.image}:${params.tag} ${params.repo}:${params.tag}
        docker push ${params.repo}:${params.tag}
    """
}

return this
