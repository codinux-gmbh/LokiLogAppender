plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)
}


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