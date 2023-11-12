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
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion

        id("io.quarkus") version quarkusVersion
        id("io.quarkus.extension") version quarkusVersion
    }
}


include("LokiLogAppenderBase")

include("LogbackLokiAppender")
include("JBossLoggingLokiAppender")


include("LogbackLokiAppenderSampleApplication")
project(":LogbackLokiAppenderSampleApplication").projectDir = File("sampleApplications/LogbackLokiAppenderSampleApplication")

include("JBossLoggingLokiAppenderSampleApplication")
project(":JBossLoggingLokiAppenderSampleApplication").projectDir = File("sampleApplications/JBossLoggingLokiAppenderSampleApplication")

/*          Quarkus             */

// building Quarkus extension with Gradle and / or Kotlin does not work (but in Quarkus?) so using Maven and Java for these

//include("QuarkusLokiLogAppender")
//project(":QuarkusLokiLogAppender").name = "quarkus-loki-log-appender"
//
//include("QuarkusLokiLogAppenderDeployment")
//project(":QuarkusLokiLogAppenderDeployment").name = "quarkus-loki-log-appender-deployment"

include("QuarkusSampleApplication")
project(":QuarkusSampleApplication").projectDir = File("sampleApplications/QuarkusSampleApplication")
