dependencies {
    // Project dependencies
    implementation(project(Modules.common))
    implementation(project(Modules.domain))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterDataJpa)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.jjwtApi)
    runtimeOnly(Dependencies.jjwtImpl)
    runtimeOnly(Dependencies.jjwtJackson)

    // XSS Sanitizer
    implementation(Dependencies.owaspJavaHtmlSanitizer)

    // Test dependencies
    testImplementation(Dependencies.springSecurityTest)
}
