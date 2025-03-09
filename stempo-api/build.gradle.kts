import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.named<BootJar>("bootJar") {
    enabled = true

    dependsOn("processAot")

    // JAR 파일에 config 디렉토리 추가
    from("${rootProject.projectDir}/config") {
        into("config")
    }

    layered {
        enabled = true
    }

    archiveBaseName.set(project.name)
    archiveVersion.set("${project.version}")
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")

    mainClass.set("com.stempo.ApiApplication")
}

tasks.named<ProcessAot>("processAot") {
    enabled = true
    dependsOn("classes")
}

dependencies {
    // Project dependencies
    implementation(project(Modules.application))
    implementation(project(Modules.auth))
    implementation(project(Modules.common))
    implementation(project(Modules.domain))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterValidation)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springDataCommons)

    // Spring Cloud Config
    implementation(Dependencies.springCloudConfigClient)
    implementation(Dependencies.springCloudBootstrapStarter)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.swagger)

    // Test dependencies
    testImplementation(Dependencies.springSecurityTest)
}
