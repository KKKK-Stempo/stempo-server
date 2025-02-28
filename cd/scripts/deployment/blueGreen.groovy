// scripts/deployment/blueGreen.groovy
/**
 * Blue-Green 배포 관련 함수들
 */

/**
 * 컨테이너를 결정하는 함수.
 *
 * @param blueContainer  : Blue 컨테이너 이름 (String)
 * @param greenContainer : Green 컨테이너 이름 (String)
 * @param blueUrl        : Blue 컨테이너 URL (String)
 * @param greenUrl       : Green 컨테이너 URL (String)
 * @param portA          : Blue 포트 (String)
 * @param portB          : Green 포트 (String)
 * @param targetMap      : 결과를 저장할 Map (예: vars)
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
    def hostPort = params.hostPort ?: null  // 미지정 시 -p 옵션 생략
    def containerPort = params.containerPort ?: "8080"

    // 기존 컨테이너 중지 및 제거
    sh """
        echo "Stopping/removing container ${params.containerName} if exists"
        if docker ps --filter 'name=${params.containerName}' --format '{{.Names}}' | grep -q '${params.containerName}'; then
            docker stop ${params.containerName}
            docker rm ${params.containerName}
        fi
    """

    // 이미지 태그: imageTag 파라미터가 제공되면 사용, 없으면 containerName 사용
    def tag = params.imageTag ?: params.containerName
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

    // extraNetworks 옵션 처리: 단일 문자열 또는 리스트
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

/**
 * 트래픽 전환 및 정리 함수.
 *
 * 필수:
 *   nginxContainer  : Nginx 컨테이너 이름 (String)
 *   newTarget       : 새로운 타겟 URL (String)
 *   nginxConfigPath : Nginx 설정 파일 경로 (String)
 *   oldPort         : 기존 포트 (String)
 *   newPort         : 변경 후 포트 (String)
 *   currentContainer: 기존 컨테이너 이름 (String)
 */
def switchTrafficAndCleanup(Map params = [:]) {
    if (!params.nginxContainer || !params.newTarget || !params.nginxConfigPath ||
        !params.oldPort || !params.newPort || !params.currentContainer) {
        error "switchTrafficAndCleanup: 필요한 파라미터가 누락되었습니다."
    }
    echo "Switching traffic to new target on port ${params.newPort}."

    // Nginx 관련 함수 호출 (공통 파일)
    def nginxUtil = load 'scripts/common/nginx.groovy'
    nginxUtil.updateConfig(
        container: params.nginxContainer,
        targetUrl: params.newTarget,
        configFile: params.nginxConfigPath,
        oldPort: params.oldPort,
        newPort: params.newPort
    )
    nginxUtil.reloadConfig(container: params.nginxContainer)

    // 기존 컨테이너 중지 및 제거
    def current = params.currentContainer
    def isRunning = sh(script: "docker ps --filter 'name=${current}' --format '{{.Names}}' | grep -q '${current}'", returnStatus: true) == 0
    if (isRunning) {
        sh """
            docker stop ${current}
            docker rm ${current}
            echo "Stopped and removed ${current}."
        """
    } else {
        echo "No running container ${current} found."
    }
}

return this
