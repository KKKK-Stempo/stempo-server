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
    implementation(Dependencies.h2database)
    implementation(Dependencies.querydslJpa)
    annotationProcessor(Dependencies.querydslApt)
    annotationProcessor(Dependencies.jakartaAnnotationApi)
    annotationProcessor(Dependencies.jakartaPersistenceApi)

    // Util
    implementation(Dependencies.jakartaValidationApi)
    implementation(Dependencies.hibernateValidator)
    implementation(Dependencies.commonsIo)
}

val querydslDir = layout.buildDirectory.dir("generated/querydsl").get().asFile

sourceSets {
    named("main") {
        java {
            srcDir(querydslDir)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.generatedSourceOutputDirectory.set(querydslDir)
}

tasks.named("clean") {
    doLast {
        querydslDir.deleteRecursively()
    }
}
