dependencies {
    // Project dependencies
    implementation(project(":stempo-common"))
    implementation(project(":stempo-domain"))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterDataJpa)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.jjwtApi)
    runtimeOnly(Dependencies.jjwtImpl)
    runtimeOnly(Dependencies.jjwtJackson)

    // Test dependencies
    testImplementation(Dependencies.springSecurityTest)
}
