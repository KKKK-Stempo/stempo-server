import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.named<BootJar>("bootJar") {
    enabled = true

    layered {
        enabled = true
    }

    archiveBaseName.set(project.name)
    archiveVersion.set("${project.version}")
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")

    mainClass.set("com.stempo.ApiApplication")
}

dependencies {
    // Project dependencies
    implementation(project(Modules.application))
    implementation(project(Modules.common))
    implementation(project(Modules.domain))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterValidation)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootActuator)
    implementation(Dependencies.springDataCommons)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.swagger)

    // Test dependencies
    testImplementation(Dependencies.springSecurityTest)
}
