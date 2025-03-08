import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.aot.ProcessTestAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version Versions.springDependencyManagement
    id("org.sonarqube") version Versions.sonarQube
    id("checkstyle")
    id("jacoco")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":stempo-api"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<Jar>("bootJar") {
    enabled = false
}

allprojects {
    group = "com.stempo"
    version = "0.0.1"

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "org.springframework.boot.aot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.sonarqube")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")

    ext["springConfigLocation"] = "${rootProject.projectDir}/config/"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.named<Jar>("jar") {
        enabled = true
    }

    tasks.named<BootJar>("bootJar") {
        enabled = false
    }

    tasks.named<ProcessAot>("processAot") {
        enabled = false
    }

    tasks.named<ProcessTestAot>("processTestAot") {
        enabled = false
    }

    sonar {
        properties {
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.organization", "kkkk-stempo")
            property("sonar.projectKey", "KKKK-Stempo_stempo-server")
            property("sonar.java.checkstyle.reportPaths", "build/reports/checkstyle/*.xml")
            property("sonar.java.coveragePlugin", "jacoco")
            property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        }
    }

    jacoco {
        toolVersion = Versions.jacoco
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.jacocoTestCoverageVerification {
        dependsOn(tasks.jacocoTestReport)

        violationRules {
            rule {
                limit {
                    minimum = "0.50".toBigDecimal()
                }
            }

            rule {
                isEnabled = true
                element = "CLASS"

                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.50".toBigDecimal()
                }

                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.50".toBigDecimal()
                }

                limit {
                    counter = "LINE"
                    value = "TOTALCOUNT"
                    maximum = "300".toBigDecimal()
                }

                excludes = listOf(
                    "com.stempo.**.*Test*",
                    "com.stempo.**.*Constants*",
                    "com.stempo.**.*Dto*",
                    "com.stempo.**.*Entity*",
                    "com.stempo.**.*Service.*",
                    "com.stempo.**.*Repository.*",
                    "com.stempo.**.*Exception*",
                    "com.stempo.**.AuthorizeRequestsCustomizer*",
                    "com.stempo.**.SecurityConfig*",
                    "com.stempo.ApiApplication*",
                )
            }
        }
    }

    checkstyle {
        toolVersion = Versions.checkStyle
        configFile = file("${rootProject.projectDir}/config/checkstyle/checkstyle.xml")
        configProperties["suppressionsFile"] =
            file("${rootProject.projectDir}/config/checkstyle/checkstyle-suppressions.xml")
    }

    tasks.withType<Checkstyle>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(Dependencies.springBootStarter)
        testImplementation(Dependencies.springBootStarterTest)
        compileOnly(Dependencies.lombok)
        annotationProcessor(Dependencies.lombok)
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()

        reports {
            junitXml.required.set(true)
        }
    }

    tasks.register("testCoverage") {
        group = "verification"
        description = "Runs the unit tests and generates a coverage report"

        dependsOn(tasks.test)
        dependsOn(tasks.jacocoTestReport)
        dependsOn(tasks.jacocoTestCoverageVerification)

        tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
        tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
    }

    tasks.withType<JavaExec> {
        ext["springConfigLocation"]?.let { systemProperty("spring.config.additional-location", it) }
    }

    tasks.named("checkstyleMain") {
        dependsOn("compileTestJava")
    }
}
