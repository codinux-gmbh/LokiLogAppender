plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)
}


group = "net.codinux.log.web"


val coroutinesVersion: String by project
val jacksonVersion: String by project

val junitVersion: String by project

dependencies {
    api(project(":LokiLogAppenderBase"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")


    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


ext["customArtifactId"] = "loki-java-web-client"
ext["description"] = "An implementation of Loki WebClient interface built with Java HttpClient which can be used instead of default implementation with Ktor"

apply(from = "../../gradle/scripts/publish-codinux.gradle.kts")