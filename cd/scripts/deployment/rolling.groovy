// scripts/deployment/rolling.groovy
/**
 * 새 인스턴스를 배포하는 함수 (Blue-Green 배포용)
 *
 * 필수:
 *   containerName   : 새 컨테이너 이름 (String)
 *   image           : 이미지 이름 (String)
 *
 * 옵션:
 *   hostPort      - 호스트 포트 (지정하지 않으면 -p 옵션 미포함)
 *   containerPort - 컨테이너 포트 (기본값: "8080")
 *   imageTag      - 도커 이미지 태그 (선택, 지정하지 않으면 containerName 값 사용)
 *   network       - 애플리케이션 네트워크 (지정하지 않으면 --network 옵션 미포함)
 *   restartPolicy - 재시작 정책 (예: "always", "on-failure" 등; 지정하지 않으면 미포함)
 *   volumes       - 볼륨 옵션 리스트 (각 항목은 "hostPath:containerPath" 형식의 문자열; 자동으로 "-v " 접두사 추가)
 *   envVars       - 환경 변수 옵션 리스트 (각 항목은 "KEY=VALUE" 형식의 문자열; 자동으로 "-e " 접두사 추가)
 *   extraNetworks - 추가로 연결할 네트워크 (단일 문자열 또는 리스트)
 *   extraArgs     - docker run 명령어에 추가할 기타 옵션 (List)
 *
 * docker run 예시:
 *   docker run -d --name <containerName> -p <hostPort>:<containerPort> [--network <network>] [--restart <restartPolicy>]
 *       [ <볼륨옵션> ] [ <환경변수옵션> ] [ <extraArgs>... ] <image>:<imageTag>
 */
def deployNewInstance(Map params = [:]) {
    if (!params.containerName || !params.image) {
        error "deployNewInstance (blue-green): 'containerName'와 'image' 파라미터는 필수입니다."
    }

    // 기본값 설정
    def hostPort = params.hostPort ?: null
    def containerPort = params.containerPort ?: "8080"
    def tag = params.imageTag ?: params.containerName

    // 기존 컨테이너 중지 및 제거
    sh """
        echo "Stopping/removing container ${params.containerName} if exists"
        if docker ps --filter 'name=${params.containerName}' --format '{{.Names}}' | grep -q '${params.containerName}'; then
            docker stop ${params.containerName}
            docker rm ${params.containerName}
        fi
    """

    // 이미지 태그: imageTag 파라미터가 제공되면 사용, 없으면 containerName 사용
    cmd += " ${params.image}:${tag}"

    // docker run 명령어 동적 구성
    def cmd = "docker run -d --name ${params.containerName}"

    if (hostPort) {
        cmd += " -p ${hostPort}:${containerPort}"
    }
    if (params.network) {
        cmd += " --network ${params.network}"
    }
    if (params.restartPolicy) {
        cmd += " --restart ${params.restartPolicy}"
    }
    if (params.volumes && params.volumes instanceof List) {
        def volumeStr = params.volumes.collect { vol ->
            vol.trim().startsWith("-v") ? vol.trim() : "-v " + vol.trim()
        }.join(" ")
        cmd += " " + volumeStr
    }
    if (params.envVars && params.envVars instanceof List) {
        def envStr = params.envVars.collect { envVar ->
            envVar.trim().startsWith("-e") ? envVar.trim() : "-e " + envVar.trim()
        }.join(" ")
        cmd += " " + envStr
    }
    if (params.extraArgs) {
        if (params.extraArgs instanceof List) {
            cmd += " " + params.extraArgs.join(" ")
        } else {
            cmd += " " + params.extraArgs
        }
    }

    echo "Executing: ${cmd}"
    sh cmd

    if (params.extraNetworks) {
        if (params.extraNetworks instanceof List) {
            params.extraNetworks.each { net ->
                def netCheck = sh(script: "docker network ls --format '{{.Name}}' | grep -q '^${net}\$'", returnStatus: true)
                if (netCheck == 0) {
                    sh "docker network connect ${net} ${params.containerName}"
                } else {
                    echo "Network ${net} not found. Skipping network connection."
                }
            }
        } else {
            def netCheck = sh(script: "docker network ls --format '{{.Name}}' | grep -q '^${params.extraNetworks}\$'", returnStatus: true)
            if (netCheck == 0) {
                sh "docker network connect ${params.extraNetworks} ${params.containerName}"
            } else {
                echo "Network ${params.extraNetworks} not found. Skipping network connection."
            }
        }
    }

    // 실행된 컨테이너 목록 출력
    sh "docker ps -a"
}

return this
