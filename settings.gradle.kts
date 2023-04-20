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


include("LokiLogAppenderBase")
project(":LokiLogAppenderBase").name = "loki-log-appender-base"
