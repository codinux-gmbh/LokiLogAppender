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
                implementation("io.ktor:ktor-client-core:$ktorVersion")

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


ext["groupId"] = group
ext["artifactId"] = "spotify-api" // is overwritten by Gradle anyway, therefore we need to set in in settings.gradle
ext["artifactVersion"] = version
ext["libraryName"] = ext["artifactId"]

ext["sourceCodeRepositoryBaseUrl"] = "https://github.com/codinux/LokiLogger"

ext["useNewSonatypeRepo"] = true
ext["packageGroup"] = "net.codinux"

ext["projectDescription"] = "Logger implementation to push logs to Loki (Like Prometheus, but for logs)"

ext["developerId"] = "codinux"
ext["developerName"] = "codinux GmbH & Co. KG"
ext["developerMail"] = "git@codinux.net"

ext["licenseName"] = "The Apache License, Version 2.0"
ext["licenseUrl"] = "http://www.apache.org/licenses/LICENSE-2.0.txt"

apply(from = "../publish.gradle.kts")
