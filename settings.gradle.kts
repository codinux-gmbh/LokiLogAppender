pluginManagement {
    val kotlinVersion: String by settings
    val quarkusVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("multiplatform") version kotlinVersion apply(false)

        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion

        id("io.quarkus") version quarkusVersion
    }
}


include("LokiLogAppenderBase")

include("LogbackLokiAppender")
include("JBossLoggingLokiAppender")


include("LogbackLokiAppenderSampleApplication")
project(":LogbackLokiAppenderSampleApplication").projectDir =
    File("sampleApplications/LogbackLokiAppenderSampleApplication")

include("JBossLoggingLokiAppenderSampleApplication")
project(":JBossLoggingLokiAppenderSampleApplication").projectDir =
    File("sampleApplications/JBossLoggingLokiAppenderSampleApplication")

include("JavaWebClient")
project(":JavaWebClient").projectDir = File("web/JavaWebClient")


/*          Quarkus             */

// building Quarkus extension with Gradle and / or Kotlin does not work so using Maven and Java for these


/*          Sample applications             */

include("QuarkusSampleApplication")
project(":QuarkusSampleApplication").projectDir = File("sampleApplications/QuarkusSampleApplication")