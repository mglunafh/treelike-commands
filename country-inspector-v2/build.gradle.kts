plugins {
    kotlin("jvm")
    application
    id("com.gradleup.shadow")
}

val projectVersion: String by project
val jacksonVersion: String by project

group = "org.some.project.kotlin.countryinspector.v2"
version = projectVersion

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
}

application {
    mainClass.set("org.some.project.kotlin.countryinspector.v2.MainV2Kt")
}
