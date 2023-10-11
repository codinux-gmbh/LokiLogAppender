plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":JBossLoggingLokiAppender"))

    implementation("org.jboss.logging:jboss-logging:3.4.2.Final")

    implementation("org.jboss.slf4j:slf4j-jboss-logging:1.2.1.Final")
}