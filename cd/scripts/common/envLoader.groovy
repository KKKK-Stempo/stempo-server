// scripts/common/envLoader.groovy
/**
 * 표준화된 매핑 테이블(mappingTable)을 이용하여 YAML 설정 파일로부터 환경변수를 로딩합니다.
 *
 * 각 서비스는 공통으로 사용해야 하는 항목들을 미리 매핑 테이블에 정의합니다.
 *
 * 매핑 테이블 예시:
 * [
 *   [yamlPath: "jenkins-domain", targetKey: "JENKINS_DOMAIN", required: true],
 *   [yamlPath: "slack.webhook-url", targetKey: "SLACK_WEBHOOK_URL", required: true],
 *   [yamlPath: "slack.color-success", targetKey: "SLACK_COLOR_SUCCESS", required: true],
 *   [yamlPath: "slack.color-failure", targetKey: "SLACK_COLOR_FAILURE", required: true],
 *   [yamlPath: "mariadb.user", targetKey: "MARIA_DB_USER", required: true],
 *   [yamlPath: "mariadb.password", targetKey: "MARIA_DB_PASSWORD", required: true],
 *   [yamlPath: "mariadb.backup-dir", targetKey: "BACKUP_DIR", required: true],
 *   [yamlPath: "dockerhub.repo", targetKey: "DOCKER_HUB_REPO", required: true],
 *   [yamlPath: "host.default-path", targetKey: "HOST_DEFAULT_PATH", required: true],
 *   [yamlPath: "host.logs-path", targetKey: "HOST_LOGS_PATH", required: true],
 *   [yamlPath: "container.default-path", targetKey: "CONTAINER_DEFAULT_PATH", required: true],
 *   [yamlPath: "container.logs-path", targetKey: "CONTAINER_LOGS_PATH", required: true],
 *   [yamlPath: "containers.blue", targetKey: "BLUE_CONTAINER", required: true],
 *   [yamlPath: "containers.green", targetKey: "GREEN_CONTAINER", required: true],
 *   [yamlPath: "containers.blue-url", targetKey: "BLUE_URL", required: true],
 *   [yamlPath: "containers.green-url", targetKey: "GREEN_URL", required: true],
 *   [yamlPath: "containers.image-name", targetKey: "IMAGE_NAME", required: true],
 *   [yamlPath: "containers.container-name", targetKey: "CONTAINER_NAME", required: true],
 *   [yamlPath: "containers.app-port", targetKey: "APP_PORT", required: true],
 *   [yamlPath: "networks.application", targetKey: "APPLICATION_NETWORK", required: true],
 *   [yamlPath: "networks.infra", targetKey: "INFRA_NETWORK", required: true],
 *   [yamlPath: "spring.profile", targetKey: "PROFILE", required: true],
 *   [yamlPath: "spring.port-a", targetKey: "PORT_A", required: true],
 *   [yamlPath: "spring.port-b", targetKey: "PORT_B", required: true],
 *   [yamlPath: "admin.username", targetKey: "WHITELIST_ADMIN_USERNAME", required: true],
 *   [yamlPath: "admin.password", targetKey: "WHITELIST_ADMIN_PASSWORD", required: true],
 *   [yamlPath: "docker.dockerfile-path", targetKey: "DOCKERFILE_PATH", required: true],
 *   [yamlPath: "docker.nginx-container-name", targetKey: "NGINX_CONTAINER_NAME", required: true],
 *   [yamlPath: "docker.mariadb-container-name", targetKey: "MARIA_DB_CONTAINER_NAME", required: true],
 *   [yamlPath: "docker.nginx-config-path", targetKey: "NGINX_CONFIG_PATH", required: true],
 *   [yamlPath: "docker.build-context", targetKey: "BUILD_CONTEXT", required: true],
 *   [yamlPath: "healthcheck.actuator-path", targetKey: "ACTUATOR_PATH", required: true],
 *   [yamlPath: "profile.resources-path", targetKey: "RESOURCES_PATH", required: true],
 *   [yamlPath: "profile.file-name", targetKey: "PROFILE_FILE_NAME", required: true]
 * ]
 *
 * @param configFile : YAML 설정 파일의 경로 (String)
 * @param targetMap : 값을 저장할 대상 Map (예: 파이프라인 변수 Map)
 * @param mappingTable : 각 항목의 매핑 정보 리스트 (List of Map)
 */
def loadEnvironmentVariables(Map params = [:]) {
    if (!params.configFile || !params.targetMap || !params.mappingTable) {
        error "loadEnvironmentVariables: configFile, targetMap, mappingTable 파라미터가 필요합니다."
    }

    // YAML 파일 읽기
    def config = readYaml(file: params.configFile)

    // 매핑 테이블을 순회하면서 각 항목의 값을 가져와서 targetMap에 저장
    params.mappingTable.each { mapping ->
        // yamlPath가 "a.b.c" 형식이면 점(.)으로 구분하여 순차적으로 값을 가져옴
        def keys = mapping.yamlPath.tokenize('.')
        def value = config
        keys.each { key ->
            if (value && value.containsKey(key)) {
                value = value[key]
            } else {
                value = null
                return
            }
        }

        // 값이 없을 경우, required가 true이면 에러 발생, 아니면 defaultValue가 있으면 사용
        if (value == null) {
            if (mapping.required) {
                error "필수 환경변수 [${mapping.targetKey}] (YAML 경로: ${mapping.yamlPath})가 누락되었습니다."
            } else if (mapping.defaultValue != null) {
                value = mapping.defaultValue
            }
        }

        // 대상 Map에 값 저장
        params.targetMap[mapping.targetKey] = value
    }
}

return this
