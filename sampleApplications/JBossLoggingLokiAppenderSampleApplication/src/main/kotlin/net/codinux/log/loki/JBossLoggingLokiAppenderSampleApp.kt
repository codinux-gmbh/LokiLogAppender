package net.codinux.log.loki

import net.codinux.log.config.LogAppenderFieldsConfig
import net.codinux.log.config.WriterConfig
import net.codinux.log.JBossLoggingUtil
import net.codinux.log.loki.config.LokiLogAppenderConfig
import org.jboss.logging.Logger
import org.jboss.logging.NDC
import org.jboss.logmanager.MDC
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


fun main() {
    JBossLoggingLokiAppenderSampleApp().runExample()
}

class JBossLoggingLokiAppenderSampleApp {

    init {
        JBossLoggingUtil.useJBossLoggingAsJavaUtilLoggingManager()

        JBossLoggingUtil.registerLogHandler(
            // make sure Loki is running on localhost under port 3100 or adjust URL here
            JBossLoggingLokiAppender(LokiLogAppenderConfig(
                writer = WriterConfig("https://staging.dankito.net", username = "SchaugtsAmoiHer", password = "NewKidOnTheLog"),
                fields = LogAppenderFieldsConfig(
                    includeMarker = true,
                    includeNdc = true,
                    includeKubernetesInfo = false,
                    includeAppName = true,
                    appName = "TestApp"
                )
            )),
            JBossLoggingUtil.colorConsoleHandler()
        )
    }


    private val log = Logger.getLogger(JBossLoggingLokiAppenderSampleApp::class.java.name)

    fun runExample() {
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
        val slf4jLogger = LoggerFactory.getLogger(JBossLoggingLokiAppenderSampleApp::class.java)
        slf4jLogger.info(MarkerFactory.getMarker("ImportantMessageMarker"), "Log with Marker")

        TimeUnit.SECONDS.sleep(2) // JBossLoggingLokiAppender sends records asynchronously, give it some time for that

        exitProcess(0)
    }
}