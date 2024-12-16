plugins {
    kotlin("jvm")
    application
    id("com.gradleup.shadow")
}

val projectVersion: String by project

group = "org.some.project.kotlin.countryinspector.v1"
version = projectVersion

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
}

application {
    mainClass.set("org.some.project.kotlin.countryinspector.v1.MainKt")
}
