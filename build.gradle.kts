plugins {
    kotlin("jvm")
}

group = "org.some.project.kotlin"

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}

tasks.wrapper {
    gradleVersion = "8.11.1"
}
