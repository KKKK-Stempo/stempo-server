dependencies {
    // Project dependencies
    implementation(project(Modules.common))
    implementation(project(Modules.domain))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterDataJpa)

    // DB
    implementation(Dependencies.mariadbDriver)
    testRuntimeOnly(Dependencies.h2database)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.commonsIo)
}
