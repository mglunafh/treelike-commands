plugins {
    kotlin("jvm")
}

val projectVersion: String by project

group = "org.some.project.kotlin.generic-parsing"
version = projectVersion

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}