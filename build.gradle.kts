// don't know why but we have to add jvm plugin this way otherwise compilation fails
plugins {
//    val kotlinVersion: String by settings
    val kotlinVersion = "1.9.23"

    kotlin("jvm") version kotlinVersion apply(false)
}


allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }

    group = "net.codinux.log"
    version = "0.6.0-SNAPSHOT"


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
