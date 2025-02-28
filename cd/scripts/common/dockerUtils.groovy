// scripts/common/dockerUtils.groovy
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

return this
