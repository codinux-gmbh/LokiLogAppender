pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("multiplatform") version kotlinVersion
    }
}


rootProject.name = "LokiLogger"


include("LokiLogger")
project(":LokiLogger").name = "loki-logger"
