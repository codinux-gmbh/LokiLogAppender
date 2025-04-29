plugins {
    kotlin("jvm")
}

java {
    toolchain {
        // ExtLogRecord.instant needs at least Java version 9
        languageVersion.set(JavaLanguageVersion.of(9))
    }
}


val junitVersion: String by project

dependencies {
    api(project(":LokiLogAppenderBase"))

    api("net.codinux.log:jboss-logging-appender-base:$version")


    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


ext["customArtifactId"] = "jboss-logging-loki-appender"
ext["description"] = "JBoss logging appender that pushes logs directly to Loki"

apply(from = "../gradle/scripts/publish-codinux.gradle.kts")