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
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("net.codinux.log:log-appender-base:1.0.0-SNAPSHOT")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
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
            }
        }
        val jvmTest by getting

        val jsMain by getting
        val jsTest by getting

        val nativeMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-curl:$ktorVersion") // requires that curl is installed
            }
        }
        val nativeTest by getting
    }
}


ext["customArtifactId"] = "loki-log-appender-base"
ext["projectDescription"] = "Logger implementation to push logs to Loki (Like Prometheus, but for logs)"

apply(from = "../gradle/scripts/publish-codinux.gradle.kts")
