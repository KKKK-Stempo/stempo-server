dependencies {
    // Spring Project
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterDataJpa)
    implementation(Dependencies.springBootActuator)
    implementation(Dependencies.springAop)

    // Logging
    implementation(Dependencies.logbackCore)
    implementation(Dependencies.logbackClassic)
    implementation(Dependencies.logstashLogbackEncoder)

    // Util
    implementation(Dependencies.swagger)
    implementation(Dependencies.gson)
    implementation(Dependencies.commonsIo)
    implementation(Dependencies.commonText)
    implementation(Dependencies.jjwtApi)
}
