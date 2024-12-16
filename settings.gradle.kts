pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.22" apply false
        kotlin("plugin.serialization") version "1.9.22" apply false
        application
        id("com.gradleup.shadow") version "8.3.3" apply false
    }
}

rootProject.name = "treelike-commands"
include("country-inspector-v2")
include("country-inspector-v1")
include("geom-enthusiast")
include("generic-parsing")
