plugins {
    kotlin("jvm")
}

java {
    toolchain {
        // ExtLogRecord.instant needs at least Java version 9, Java HttpClient 11
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


val logAppenderBaseVersion: String by project

val junitVersion: String by project
val mockkVersion: String by project
val assertKVersion: String by project

dependencies {
    api(project(":LokiLogAppenderBase"))
    implementation(project(":JavaWebClient"))

    api("net.codinux.log:jboss-logging-appender-base:$logAppenderBaseVersion")


    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.willowtreeapps.assertk:assertk:$assertKVersion")

    testImplementation("org.jboss.slf4j:slf4j-jboss-logging:1.2.1.Final")
}

tasks.test {
    useJUnitPlatform()
}


ext["customArtifactId"] = "jboss-logging-loki-appender"
ext["description"] = "JBoss logging appender that pushes logs directly to Loki"

apply(from = "../gradle/scripts/publish-codinux.gradle.kts")