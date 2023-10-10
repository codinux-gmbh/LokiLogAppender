plugins {
    kotlin("jvm")
}

java {
    withSourcesJar()

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

    // for LokiJBossLoggingAppenderSample
    testImplementation("org.jboss.logging:jboss-logging:3.4.2.Final")

    testImplementation("org.jboss.slf4j:slf4j-jboss-logging:1.2.1.Final")
}

tasks.test {
    useJUnitPlatform()
}


ext["customArtifactId"] = "jboss-logging-loki-appender"
ext["description"] = "JBoss logging appender that pushes logs directly to Loki"

apply(from = "../gradle/scripts/publish-codinux.gradle.kts")