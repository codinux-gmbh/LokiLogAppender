plugins {
    id("java")
    id("io.quarkus.extension")
}

quarkusExtension {
    deploymentModule = ":quarkus-loki-logger-deployment"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


val quarkusVersion: String by project
val junitVersion: String by project

dependencies {
    implementation(platform("io.quarkus:quarkus-bom:${quarkusVersion}"))
    implementation("io.quarkus:quarkus-core")

    implementation("$group:loki-jboss-logging-appender:$version")
//    api("$group:loki-jboss-logging-appender:$version")
//    api(project(":loki-jboss-logging-appender"))

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


ext["artifactId"] = project.name
ext["libraryName"] = ext["artifactId"]

ext["description"] = "Quarkus extension to enable pushing logs to Loki in Quarkus"


val commonScriptsFile = File(File(project.gradle.gradleUserHomeDir, "scripts"), "commonScripts.gradle")
if (commonScriptsFile.exists()) {
    apply(from = commonScriptsFile)
}