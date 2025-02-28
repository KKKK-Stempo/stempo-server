// scripts/common/db.groovy
/**
 * MariaDB 백업을 수행하는 함수.
 *
 * @param dbUser : DB 사용자 (String)
 * @param dbPassword : DB 비밀번호 (String)
 * @param containerName : MariaDB 컨테이너 이름 (String)
 * @param backupDir : 백업 디렉토리 경로 (String)
 */
def backupMariaDB(Map params = [:]) {
    if (!params.dbUser || !params.dbPassword || !params.containerName || !params.backupDir) {
        error "backupMariaDB: 필수 파라미터(dbUser, dbPassword, containerName, backupDir)가 필요합니다."
    }
    def backupFile = "mariadb_backup_${new Date().format('yyyy-MM-dd_HH-mm-ss')}.sql"
    sh """
        echo "Backing up MariaDB database to ${params.backupDir}/${backupFile}..."
        docker exec -e MYSQL_PWD=${params.dbPassword} ${params.containerName} sh -c 'mariadb-dump -u ${params.dbUser} stempo > ${params.backupDir}/${backupFile}'
    """
    return backupFile
}

return this
