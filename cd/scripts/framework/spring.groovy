// cd/scripts/framework/spring.groovy
/**
 * Spring 관련 모듈 프로파일 설정 함수.
 *
 * @param credentialsId : Jenkins 크리덴셜 ID (String)
 * @param resourcePath : 대상 리소스 디렉토리 (String)
 * @param profileFile : 대상 파일명 (String)
 */
def configureModuleProfile(Map params = [:]) {
    if (!params.credentialsId || !params.resourcePath || !params.profileFile) {
        error "configureModuleProfile: 필요한 파라미터가 누락되었습니다."
    }
    withCredentials([file(credentialsId: params.credentialsId, variable: 'YML_FILE')]) {
        def targetPath = "${params.resourcePath}/${params.profileFile}"
        sh """
            mkdir -p ${params.resourcePath}
            cp \$YML_FILE ${targetPath}
        """
    }
}

return this
