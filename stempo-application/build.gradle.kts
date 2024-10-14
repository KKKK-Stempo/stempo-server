dependencies {
    // Project dependencies
    implementation(project(":stempo-auth"))
    implementation(project(":stempo-common"))
    implementation(project(":stempo-domain"))
    implementation(project(":stempo-infrastructure"))

    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterDataJpa)
    implementation(Dependencies.springDataCommons)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.hibernateValidator)
    implementation(Dependencies.swagger)
    implementation(Dependencies.commonsIo)
}
