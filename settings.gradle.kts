pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
    }
}


include("LokiLogAppenderBase")
project(":LokiLogAppenderBase").name = "loki-log-appender-base"

include("LokiJBossLoggingAppender")
project(":LokiJBossLoggingAppender").name = "loki-jboss-logging-appender"
