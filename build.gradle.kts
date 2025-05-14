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
    version = "0.7.0"


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
    val parentPomTextUpdated = parentPomText.replaceFirst(parentPomVersionRegex, "    <version>$version</version>")
    parentPomFile.writeText(parentPomTextUpdated)

    listOf(projectDir.resolve("QuarkusLokiLogger"), projectDir.resolve("QuarkusLokiLoggerDeployment"))
        .map { it.resolve("pom.xml") }
        .forEach { childPomFile ->
            val childPomText = childPomFile.readText()
            val childPomTextUpdated = childPomText.replaceFirst(childPomVersionRegex, "        <version>$version</version>")
            childPomFile.writeText(childPomTextUpdated)
        }
}
