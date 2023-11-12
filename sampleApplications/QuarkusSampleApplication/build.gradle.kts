plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.quarkus")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


val quarkusVersion: String by project

dependencies {
    implementation(platform("io.quarkus:quarkus-bom:$quarkusVersion"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-resteasy-jackson")

    implementation("$group:quarkus-loki-log-appender:$version")
    implementation("net.codinux.log:kmp-log:1.0.0")
}


allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.javaParameters = true
}
