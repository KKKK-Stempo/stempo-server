// scripts/deployment/rolling.groovy
/**
 * 새 인스턴스를 배포하는 함수 (Rolling 배포용)
 *
 * 필수: name (컨테이너 이름), image (이미지 이름)
 * 옵션:
 *   hostPort       - 호스트 포트 (지정하지 않으면 -p 옵션 미포함)
 *   containerPort  - 컨테이너 포트 (기본값: "5000")
 *   imageTag       - 도커 이미지 태그 (지정하지 않으면 기본적으로 'name' 사용)
 *   network        - 애플리케이션 네트워크 (지정하지 않으면 --network 옵션 미포함)
 *   restartPolicy  - 재시작 정책 (예: "always", "on-failure" 등; 지정하지 않으면 미포함)
 *   extraNetworks  - 추가로 연결할 네트워크 (단일 문자열 또는 리스트, 지정하지 않으면 연결 시도하지 않음)
 *   extraArgs      - docker run 명령어에 추가할 기타 옵션 (List, 선택)
 *
 * 예를 들어, docker run 명령어는 다음과 같이 구성됩니다:
 *   docker run -d --name <name> -p <hostPort>:<containerPort> [--network <network>] [--restart <restartPolicy>] [<extraArgs>...] <image>:<imageTag>
 */
def deployNewInstance(Map params = [:]) {
    if (!params.name || !params.image) {
        error "deployNewInstance (rolling): 'name'와 'image' 파라미터는 필수입니다."
    }

    // 기존 컨테이너 중지 및 제거
    sh """
        echo "Stopping/removing ${params.name} if exists"
        if docker ps --filter 'name=${params.name}' --format '{{.Names}}' | grep -q '${params.name}'; then
            docker stop ${params.name}
            docker rm ${params.name}
        fi
    """

    // 기본값 설정: containerPort 기본 "5000", imageTag 기본 'name'
    def containerPort = params.containerPort ?: "5000"
    def tag = params.imageTag ?: params.name

    // docker run 명령어 동적 구성
    def cmd = "docker run -d --name ${params.name}"

    if (params.hostPort) {
        cmd += " -p ${params.hostPort}:${containerPort}"
    }
    if (params.network) {
        cmd += " --network ${params.network}"
    }
    if (params.restartPolicy) {
        cmd += " --restart ${params.restartPolicy}"
    }
    if (params.extraArgs) {
        if (params.extraArgs instanceof List) {
            cmd += " " + params.extraArgs.join(" ")
        } else {
            cmd += " " + params.extraArgs
        }
    }
    // 이미지 태그는 별도의 파라미터 imageTag로 처리 (없으면 기본적으로 name 사용)
    cmd += " ${params.image}:${tag}"

    echo "Executing: ${cmd}"
    sh cmd

    // extraNetworks 옵션 처리: 단일 문자열 또는 리스트 모두 처리
    if (params.extraNetworks) {
        if (params.extraNetworks instanceof List) {
            params.extraNetworks.each { net ->
                def netCheck = sh(script: "docker network ls --format '{{.Name}}' | grep -q '^${net}\$'", returnStatus: true)
                if (netCheck == 0) {
                    sh "docker network connect ${net} ${params.name}"
                } else {
                    echo "Network ${net} not found. Skipping network connection."
                }
            }
        } else {
            def netCheck = sh(script: "docker network ls --format '{{.Name}}' | grep -q '^${params.extraNetworks}\$'", returnStatus: true)
            if (netCheck == 0) {
                sh "docker network connect ${params.extraNetworks} ${params.name}"
            } else {
                echo "Network ${params.extraNetworks} not found. Skipping network connection."
            }
        }
    }

    // 실행된 컨테이너 목록 출력
    sh "docker ps -a"
}

return this
