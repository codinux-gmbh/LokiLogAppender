plugins {
    id("java")
}

java {
    toolchain {
        // ExtLogRecord.instant needs at least Java version 9 (why?)
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


val quarkusVersion: String by project

dependencies {
    implementation(platform("io.quarkus:quarkus-bom:${quarkusVersion}"))
    implementation("io.quarkus:quarkus-core-deployment")

//    implementation(project(":quarkus-loki-logger"))
    implementation("net.codinux.log:quarkus-loki-logger:$version")


    testImplementation("io.quarkus:quarkus-junit5-internal")
}

tasks.test {
    useJUnitPlatform()
}