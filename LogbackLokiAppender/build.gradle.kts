plugins {
    kotlin("jvm")
}

java {
    toolchain {
        // ExtLogRecord.instant needs at least Java version 9, Java HttpClient 11
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


val junitVersion: String by project

dependencies {
    api(project(":LokiLogAppenderBase"))
    implementation(project(":JavaWebClient"))

    api("net.codinux.log:logback-appender-base:$version")


    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


ext["customArtifactId"] = "logback-loki-appender"
ext["description"] = "Logback appender that pushes logs directly to Loki"

apply(from = "../gradle/scripts/publish-codinux.gradle.kts")