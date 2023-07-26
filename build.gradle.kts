plugins {
    kotlin("jvm") version "1.9.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "org.some.project.kotlin"
version = "0.1-SNAPSHOT"

subprojects {

    repositories {
        mavenCentral()
    }
}
