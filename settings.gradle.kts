pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.22"
        application
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}

rootProject.name = "treelike-commands"
include("country-inspector-v2")
include("country-inspector-v1")
include("geom-enthusiast")
include("generic-parsing")
