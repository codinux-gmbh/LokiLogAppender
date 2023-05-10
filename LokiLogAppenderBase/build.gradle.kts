plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }

        nodejs {

        }
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    val coroutinesVersion: String by project
    val ktorVersion: String by project
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("net.codinux.log:log-appender-base:1.0.0-SNAPSHOT")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

                // why to we have to re-add this dependency, it's exported with api() by log-appender-base
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
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

                // why to we have to re-add this dependency, it's exported with api() by log-appender-base
                api("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0")
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


ext["artifactId"] = "loki-log-appender-base" // is overwritten by Gradle anyway, therefore we need to set in in settings.gradle
ext["libraryName"] = ext["artifactId"]

ext["projectDescription"] = "Logger implementation to push logs to Loki (Like Prometheus, but for logs)"

apply(from = "../publish.gradle.kts")
