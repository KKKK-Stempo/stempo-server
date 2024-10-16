object Dependencies {
    const val springBootStarter = "org.springframework.boot:spring-boot-starter"
    const val springBootStarterWeb = "org.springframework.boot:spring-boot-starter-web"
    const val springBootStarterValidation = "org.springframework.boot:spring-boot-starter-validation"
    const val springBootStarterSecurity = "org.springframework.boot:spring-boot-starter-security"
    const val springBootActuator = "org.springframework.boot:spring-boot-starter-actuator"
    const val springBootStarterDataJpa = "org.springframework.boot:spring-boot-starter-data-jpa"
    const val springDataCommons = "org.springframework.data:spring-data-commons"
    const val jakartaValidationApi = "jakarta.validation:jakarta.validation-api:${Versions.jakartaValidationApi}"
    const val hibernateValidator = "org.hibernate.validator:hibernate-validator:${Versions.hibernateValidator}"
    const val swagger = "org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.swagger}"
    const val mariadbDriver = "org.mariadb.jdbc:mariadb-java-client:${Versions.mariadb}"
    const val lombok = "org.projectlombok:lombok"
    const val jjwtApi = "io.jsonwebtoken:jjwt-api:${Versions.jjwt}"
    const val jjwtImpl = "io.jsonwebtoken:jjwt-impl:${Versions.jjwt}"
    const val jjwtJackson = "io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}"
    const val commonsIo = "commons-io:commons-io:${Versions.commonsIo}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    // Test dependencies
    const val springBootStarterTest = "org.springframework.boot:spring-boot-starter-test"
    const val springSecurityTest = "org.springframework.security:spring-security-test"
}
