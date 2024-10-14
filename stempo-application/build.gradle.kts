dependencies {
    // Project dependencies
    implementation(project(Modules.auth))
    implementation(project(Modules.common))
    implementation(project(Modules.domain))
    implementation(project(Modules.infrastructure))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterDataJpa)
    implementation(Dependencies.springDataCommons)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.hibernateValidator)
    implementation(Dependencies.swagger)
}
