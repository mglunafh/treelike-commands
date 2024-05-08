plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    id("com.github.johnrengelman.shadow")
}

val projectVersion: String by project
val jacksonVersion: String by project

group = "org.some.project.kotlin"
version = projectVersion

dependencies {
    implementation(project(":generic-parsing"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

application {
    mainClass.set("org.some.project.kotlin.geometry.GeometryMainKt")
}
