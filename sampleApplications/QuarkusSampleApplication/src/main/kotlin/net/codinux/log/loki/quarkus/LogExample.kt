package net.codinux.log.loki.quarkus

import io.quarkus.runtime.Startup
import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import org.jboss.logging.Logger
import org.jboss.logging.NDC
import org.jboss.logmanager.MDC
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory

@Startup
@Singleton
class LogExample {

    private val log = Logger.getLogger(LogExample::class.java.name)


    @PostConstruct
    internal fun init() {
        showLogExample()
    }

    private fun showLogExample() {
        log.error("Error without Exception")
        log.error("Error with Exception", Exception("A test exception for error log level"))

        log.warn("Warn without Exception")
        log.warn("Warn with Exception", Exception("A test exception for warn log level"))

        log.info("Info log")

        log.debug("Debug log")

        MDC.put("MDC test key", "MDC test value")
        log.info("Log with MDC set")
        MDC.clear()

        log.info("Log after clearing MDC")

        NDC.push("NDC 1")
        NDC.push("NDC 2")

        log.info("Log with NDC")

        NDC.clear()

        // Marker does not seem to be supported by JBoss logging, even though it's a field of ExtLogRecord with JavaDoc:
        // "Markers are used mostly by SLF4J and Log4j."
        val slf4jLogger = LoggerFactory.getLogger(LogExample::class.java)
        slf4jLogger.info(MarkerFactory.getMarker("ImportantMessageMarker"), "Log with Marker")
    }

}