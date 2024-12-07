import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.aot.ProcessTestAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version Versions.springDependencyManagement
    id("org.sonarqube") version Versions.sonarQube
    id("checkstyle")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":stempo-api"))
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
        }
    }

    checkstyle {
        toolVersion = Versions.checkStyle
        configFile = file("${rootProject.projectDir}/config/checkstyle/checkstyle.xml")
        configProperties["suppressionsFile"] = file("${rootProject.projectDir}/config/checkstyle/checkstyle-suppressions.xml")
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

    tasks.withType<JavaExec> {
        ext["springConfigLocation"]?.let { systemProperty("spring.config.additional-location", it) }
    }
}
