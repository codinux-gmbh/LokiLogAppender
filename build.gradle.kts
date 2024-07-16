plugins {
    // don't know why it's needed but otherwise build breaks with "The Kotlin Gradle plugin was loaded multiple times in different subprojects"
    kotlin("plugin.serialization") apply(false)
}


allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }

    group = "net.codinux.log"
    version = "0.5.4-SNAPSHOT"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/codinux/LokiLogAppender"

    ext["projectDescription"] = "Logger implementation to push logs to Loki (Like Prometheus, but for logs)"
}


tasks.register("publishAllToMavenLocal") {
    dependsOn(
        ":LokiLogAppenderBase:publishToMavenLocal",

        ":LogbackLokiAppender:publishToMavenLocal",
        ":JBossLoggingLokiAppender:publishToMavenLocal"
    )
}

tasks.register("publishAll") {
    dependsOn(
        ":LokiLogAppenderBase:publish",

        ":LogbackLokiAppender:publish",
        ":JBossLoggingLokiAppender:publish"
    )
}
