plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


val quarkusVersion: String by project

dependencies {
    implementation(platform("io.quarkus:quarkus-bom:${quarkusVersion}"))
    implementation("io.quarkus:quarkus-core-deployment")

//    implementation(project(":quarkus-loki-log-appender"))
    implementation("net.codinux.log:quarkus-loki-log-appender:$version")


    testImplementation("io.quarkus:quarkus-junit5-internal")
}

tasks.test {
    useJUnitPlatform()
}


ext["artifactId"] = project.name
ext["libraryName"] = ext["artifactId"]

ext["description"] = "The deployment module of the Loki Quarkus extension"


val commonScriptsFile = File(File(project.gradle.gradleUserHomeDir, "scripts"), "commonScripts.gradle")
if (commonScriptsFile.exists()) {
    apply(from = commonScriptsFile)
}