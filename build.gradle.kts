// don't know why but we have to add jvm plugin this way otherwise compilation fails
plugins {
//    val kotlinVersion: String by settings
    val kotlinVersion = "1.9.25"

    kotlin("jvm") version kotlinVersion apply(false)
}


allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }

    group = "net.codinux.log"
    version = "0.8.0-SNAPSHOT"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/codinux-gmbh/LokiLogAppender"

    ext["projectDescription"] = "Logger implementation to push logs to Loki (Like Prometheus, but for logs)"
}


gradle.taskGraph.whenReady {
    setVersion(project.version.toString())
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


fun setVersion(version: String) {
    val projectDir = project.projectDir

    val parentPomVersionRegex = Regex("^    <version>[\\dSNAPSHOT.-]+</version>", RegexOption.MULTILINE)
    val childPomVersionRegex = Regex("^        <version>[\\dSNAPSHOT.-]+</version>", RegexOption.MULTILINE)

    val parentPomFile = projectDir.resolve("pom.xml")
    val parentPomText = parentPomFile.readText()
    var parentPomTextUpdated = parentPomText.replaceFirst(parentPomVersionRegex, "    <version>$version</version>")

    listOf(projectDir.resolve("QuarkusLokiLogger"), projectDir.resolve("QuarkusLokiLoggerDeployment"))
        .map { it.resolve("pom.xml") }
        .forEach { childPomFile ->
            val childPomText = childPomFile.readText()
            val childPomTextUpdated = childPomText.replaceFirst(childPomVersionRegex, "        <version>$version</version>")
            childPomFile.writeText(childPomTextUpdated)
        }


    val gradlePropertiesText = projectDir.resolve("gradle.properties").readText()

    val kotlinVersionRegex = Regex("^kotlinVersion=([\\d.]+)$", RegexOption.MULTILINE)
    val kotlinVersion = kotlinVersionRegex.find(gradlePropertiesText)?.groups!![1]!!.value

    val logAppenderBaseVersionRegex = Regex("^logAppenderBaseVersion=([\\dSNAPSHOT.-]+)$", RegexOption.MULTILINE)
    val logAppenderBaseVersion = logAppenderBaseVersionRegex.find(gradlePropertiesText)?.groups!![1]!!.value

    val parentPomKotlinVersionRegex = Regex("<kotlin.version>[\\d.]+</kotlin.version>", RegexOption.MULTILINE)
    val parentPomLogAppenderBaseVersionRegex = Regex("<log.appender.base.version>[\\dSNAPSHOT.-]+</log.appender.base.version>", RegexOption.MULTILINE)

    parentPomTextUpdated = parentPomTextUpdated.replaceFirst(parentPomKotlinVersionRegex, "<kotlin.version>$kotlinVersion</kotlin.version>")
    parentPomTextUpdated = parentPomTextUpdated.replaceFirst(parentPomLogAppenderBaseVersionRegex, "<log.appender.base.version>$logAppenderBaseVersion</log.appender.base.version>")

    parentPomFile.writeText(parentPomTextUpdated)
}
