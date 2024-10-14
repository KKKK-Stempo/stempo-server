tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    archiveBaseName.set("${project.name}")
    archiveVersion.set("${project.version}")
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

dependencies {
    // Project dependencies
    implementation(project(":stempo-application"))
    implementation(project(":stempo-common"))
    implementation(project(":stempo-domain"))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterValidation)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springDataCommons)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.swagger)

    // Test dependencies
    testImplementation(Dependencies.springSecurityTest)
}
