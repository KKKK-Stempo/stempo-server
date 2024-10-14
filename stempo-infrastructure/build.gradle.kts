dependencies {
    // Project dependencies
    implementation(project(":stempo-common"))
    implementation(project(":stempo-domain"))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterDataJpa)

    // DB
    implementation(Dependencies.mariadbDriver)

    // Util
    implementation(Dependencies.jakartaValidationApi)
}
