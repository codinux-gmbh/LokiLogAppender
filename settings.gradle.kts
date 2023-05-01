pluginManagement {
    val kotlinVersion: String by settings
    val quarkusVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion

        id("io.quarkus") version quarkusVersion
        id("io.quarkus.extension") version quarkusVersion
    }
}


include("LokiLogAppenderBase")
project(":LokiLogAppenderBase").name = "loki-log-appender-base"

include("LokiJBossLoggingAppender")
project(":LokiJBossLoggingAppender").name = "loki-jboss-logging-appender"


/*          Quarkus             */

// building Quarkus extension with Gradle and / or Kotlin does not work (but in Quarkus?) so using Maven and Java for these

//include("QuarkusLokiLogger")
//project(":QuarkusLokiLogger").name = "quarkus-loki-logger"
//
//include("QuarkusLokiLoggerDeployment")
//project(":QuarkusLokiLoggerDeployment").name = "quarkus-loki-logger-deployment"

include("QuarkusSampleApplication")
project(":QuarkusSampleApplication").projectDir = File("sampleApplications/QuarkusSampleApplication")
