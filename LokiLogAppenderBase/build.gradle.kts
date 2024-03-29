plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    // Enable the default target hierarchy:
    targetHierarchy.default()

    jvm {
        jvmToolchain(8)
        withJava()

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()

            testLogging { // This is for logging and can be removed.
                events("passed", "skipped", "failed")
            }
        }
    }

    js(IR) {
        moduleName = "loki-log-appender-base"
        binaries.executable()

        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }

        nodejs {
            testTask {
                useMocha {
                    timeout = "20s" // Mocha times out after 2 s, which is too short for bufferExceeded() test
                }
            }
        }
    }

    // wasm()


    linuxX64()
    mingwX64()

    ios {
        binaries {
            framework {
                baseName = "loki-log-appender-base"
            }
        }
    }
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchos()
    watchosSimulatorArm64()
    tvos()
    tvosSimulatorArm64()


    val coroutinesVersion: String by project
    val ktorVersion: String by project
    val kotlinxDateTimeVersion: String by project
    val kotlinSerializationVersion: String by project
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("net.codinux.log:log-appender-base:$version")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

                // why does it has to be re-added?
                api("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDateTimeVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

                // why does it has to be re-added?
                api("org.jetbrains.kotlinx:kotlinx-datetime-jvm:$kotlinxDateTimeVersion")
            }
        }
        val jvmTest by getting

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")

                // why does it has to be re-added?
                api("org.jetbrains.kotlinx:kotlinx-datetime-js:$kotlinxDateTimeVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-js:$kotlinSerializationVersion")
            }
        }
        val jsTest by getting


        val linuxMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")

                // why does it has to be re-added?
                api("org.jetbrains.kotlinx:kotlinx-datetime-linuxx64:$kotlinxDateTimeVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-linuxx64:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-linuxx64:$kotlinSerializationVersion")
            }
        }

        val mingwMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-winhttp:$ktorVersion")

                // why does it has to be re-added?
                api("org.jetbrains.kotlinx:kotlinx-datetime-mingwx64:$kotlinxDateTimeVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-mingwx64:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-mingwx64:$kotlinSerializationVersion")
            }
        }

        val appleMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
    }
}


ext["customArtifactId"] = "loki-log-appender-base"
ext["projectDescription"] = "Logger implementation to push logs to Loki (Like Prometheus, but for logs)"

apply(from = "../gradle/scripts/publish-codinux.gradle.kts")
