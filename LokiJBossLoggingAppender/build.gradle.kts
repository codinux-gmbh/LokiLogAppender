plugins {
    kotlin("jvm")
}


val junitVersion: String by project

dependencies {
//    api("$group:loki-log-appender-base:$version")
    api(project(":loki-log-appender-base"))

    api("net.codinux.log:jboss-logging-appender-base:$version")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // for LokiJBossLoggingAppenderSample
    testImplementation("org.jboss.logging:jboss-logging:3.4.2.Final")

    testImplementation("org.jboss.slf4j:slf4j-jboss-logging:1.2.1.Final")
}

tasks.test {
    useJUnitPlatform()
}