// cd/scripts/common/analyzeChanges.groovy

final def config = load "${env.WORKSPACE}/cd/scripts/common/config.groovy"

/**
 * 이전 성공 빌드부터 현재 빌드까지 변경된 파일 목록을 가져오는 함수
 */
def changedFilesSinceLastSuccess() {
    def lastSuccess = currentBuild.previousSuccessfulBuild?.number
    if (!lastSuccess) {
        println "No successful build history. Treating as full build."
        return []
    }
    def build = currentBuild
    def files = []
    while (build.number > lastSuccess) {
        build.changeSets.each { cs ->
            cs.items.each { item ->
                item.affectedFiles.each { f ->
                    files << f.path
                }
            }
        }
        build = build.getPreviousBuild()
    }
    return files.unique()
}

/**
 * 공통 경로(runAllLocations)나 루트 변경 여부를 판단하여 전체 빌드가 필요한지 결정
 */
def shouldRunAll(files, runAllLocations) {
    def hasCommon = files.any { f -> runAllLocations.any { prefix -> f.startsWith(prefix) } }
    def hasRoot = files.any { !it.contains('/') }
    return (hasCommon || hasRoot || files.isEmpty())
}

/**
 * 서비스 매핑(servicesMapping)을 이용해 변경된 서비스 목록을 추출
 */
def determineChangedServices(files, servicesMapping) {
    def changedServices = []
    servicesMapping.each { serviceName, folders ->
        def isChanged = files.any { f ->
            folders.any { folder -> f.startsWith(folder + "/") }
        }
        if (isChanged) {
            changedServices << serviceName
        }
    }
    return changedServices.unique()
}

return {
    def files = changedFilesSinceLastSuccess()
    println "Changed files: ${files}"

    // runAllLocations 설정에 해당하는 변경이 있으면 전체 빌드
    if (shouldRunAll(files, config.runAllLocations)) {
        println "Detected common/root changes. Building all services."
        return ["core", "rhythm"]
    }

    def changed = determineChangedServices(files, config.servicesMapping)
    println "Determined changed services: ${changed}"
    return changed
}
