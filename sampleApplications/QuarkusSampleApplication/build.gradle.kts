plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.quarkus")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


val quarkusVersion: String by project

dependencies {
    // try to use the latest Quarkus version to see if extension still works with newer Quarkus versions
    implementation(platform("io.quarkus:quarkus-bom:$quarkusVersion"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-resteasy-jackson")

    implementation("$group:quarkus-loki-log-appender:$version")
    implementation("net.codinux.log:klf:1.8.0")
}


allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.javaParameters = true
}
