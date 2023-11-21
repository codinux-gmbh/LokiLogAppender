package net.codinux.log.loki

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.slf4j.MarkerFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


fun main() {
    // set the configuration in your Logback config, e.g. logback.xml (see src/main/resources/logback.xml)
    // make sure Loki is running on localhost under port 3100 or adjust URL there
    LogbackLokiAppenderSampleApp().runExample()
}


open class LogbackLokiAppenderSampleApp {

    private val log = LoggerFactory.getLogger(LogbackLokiAppenderSampleApp::class.java.name)


    fun runExample() {
        MDC.put("traceId", UUID.randomUUID().toString()) // as a sample adds a traceId to all logs below

        log.error("Error without Exception")
        log.error("Error with Exception", Exception("A test exception for error log level"))

        log.warn("Warn without {}", "Exception")
        log.warn("Warn with Exception", Exception("A test exception for warn log level"))

        log.info("Info log")

        log.debug("Debug log - shouldn't get logged")

        MDC.put("MDC test key", "MDC test value")
        log.info("Log with MDC set")
        MDC.clear()

        log.info("Log after clearing MDC")

        log.info(MarkerFactory.getMarker("ImportantMessageMarker"), "Log with Marker")

        TimeUnit.SECONDS.sleep(5) // LokiLogAppender sends records asynchronously, give it some time for that

        exitProcess(0)
    }

}