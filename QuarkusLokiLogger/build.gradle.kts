plugins {
    id("java")
    id("io.quarkus.extension")
}

quarkusExtension {
    deploymentModule = ":quarkus-loki-logger-deployment"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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