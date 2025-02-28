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
        DOCKER_BUILDKIT=1 docker build -f ${env.WORKSPACE}${params.dockerfile} -t ${params.image}:${params.tag} ${params.context}
        docker tag ${params.image}:${params.tag} ${params.repo}:${params.tag}
        docker push ${params.repo}:${params.tag}
    """
}

/**
 * 주어진 컨테이너를 중지 및 제거하는 함수.
 *
 * @param containerName : 컨테이너 이름 (String)
 */
def stopAndRemoveContainer(String containerName) {
    if (!containerName) {
        error "stopAndRemoveContainer: containerName 파라미터가 필요합니다."
    }
    def isRunning = sh(script: "docker ps --filter 'name=${containerName}' --format '{{.Names}}' | grep -q '${containerName}'", returnStatus: true) == 0
    if (isRunning) {
        sh """
            docker stop ${containerName}
            docker rm ${containerName}
            echo "Stopped and removed ${containerName}."
        """
    } else {
        echo "No running container ${containerName} found."
    }
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

    cmd += " ${params.image}:${tag}"

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
