import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.aot.ProcessTestAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version Versions.springDependencyManagement
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<Jar>("bootJar") {
    enabled = false
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":stempo-api"))
}

allprojects {
    group = "com.stempo"
    version = "0.0.1"

    ext["springConfigLocation"] = "${rootProject.projectDir}/config/"

    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "org.springframework.boot.aot")
    apply(plugin = "io.spring.dependency-management")

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
    }

    tasks.withType<JavaExec> {
        ext["springConfigLocation"]?.let { systemProperty("spring.config.additional-location", it) }
    }
}
