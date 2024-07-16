plugins {
    kotlin("jvm")
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


dependencies {
    implementation(project(":JBossLoggingLokiAppender"))

    implementation("org.jboss.logging:jboss-logging:3.4.2.Final")

    implementation("org.jboss.slf4j:slf4j-jboss-logging:1.2.1.Final")
}