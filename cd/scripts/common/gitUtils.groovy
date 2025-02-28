// scripts/common/gitUtils.groovy
/**
 * Git 로그 변경 내역을 가져오는 함수.
 *
 * @param previousCommit : 이전 커밋 (기본값: HEAD~1)
 * @param currentCommit : 현재 커밋 (기본값: HEAD)
 * @param maxLines : 최대 출력 라인 수 (기본값: 10)
 */
def getChangeLog(Map params = [:]) {
    def previousCommit = params.previousCommit ?: 'HEAD~1'
    def currentCommit = params.currentCommit ?: 'HEAD'
    def maxLines = params.maxLines ?: 10

    def changeLog = sh(
        script: "git log ${previousCommit}..${currentCommit} --pretty=format:\"* %h - %s (%an)\" --abbrev-commit",
        returnStdout: true
    ).trim()

    if (!changeLog) {
        changeLog = "No changes found"
    }
    def lines = changeLog.split('\n')
    if (lines.size() > maxLines) {
        changeLog = lines.take(maxLines).join('\n') + "\n... (truncated)"
    }
    return changeLog
}

return this
