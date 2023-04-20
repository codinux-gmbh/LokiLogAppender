plugins {
    kotlin("jvm")
}

java {
    toolchain {
        // ExtLogRecord.instant needs at least Java version 9 (why?)
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


val quarkusVersion: String by project

dependencies {
    implementation(platform("io.quarkus:quarkus-bom:${quarkusVersion}"))
    implementation("io.quarkus:quarkus-core-deployment")

    implementation(project(":quarkus-loki-logger"))

    testImplementation("io.quarkus:quarkus-junit5-internal")
}

tasks.test {
    useJUnitPlatform()
}