import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // suppresses compiler warning: [EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING] 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta.
        freeCompilerArgs.add("-Xexpect-actual-classes")

        // avoid "variable has been optimised out" in debugging mode
        if (System.getProperty("idea.debugger.dispatch.addr") != null) {
            freeCompilerArgs.add("-Xdebug")
        }
    }


    jvmToolchain(8)

    jvm {
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
        binaries.library()

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

    // ktor2 does not support wasmJs
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser {
//            testTask {
//                useKarma {
//                    useChromeHeadless()
//                    useFirefoxHeadless()
//                }
//            }
//        }
//    }


    linuxX64()
    mingwX64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosArm64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()

    applyDefaultHierarchyTemplate()


    val coroutinesVersion: String by project
    val ktorVersion: String by project
    val kmpDateTimeVersion: String by project
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

                api("net.dankito.datetime:kmp-datetime:$kmpDateTimeVersion")
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
            }
        }
        val jvmTest by getting

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
        val jsTest by getting


        val linuxMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }

        val mingwMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-winhttp:$ktorVersion")
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
