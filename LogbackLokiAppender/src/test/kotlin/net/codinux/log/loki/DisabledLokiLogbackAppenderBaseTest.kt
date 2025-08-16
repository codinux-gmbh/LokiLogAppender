package net.codinux.log.loki

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.qos.logback.classic.spi.ILoggingEvent
import net.codinux.log.LogWriter
import net.codinux.log.LogbackAppenderBase
import net.codinux.log.addAppenderToRootLogger
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class DisabledLokiLogbackAppenderBaseTest {

    private val countCalls = AtomicInteger(0)

    private val underTest = object : LogbackAppenderBase() {
        override fun createLogWriter(): LogWriter? = null

        override fun appendEvent(logWriter: LogWriter, event: ILoggingEvent) {
            countCalls.incrementAndGet()
        }
    }.apply {
        this.start()
    }

    private val log = LoggerFactory.getLogger(DisabledLokiLogbackAppenderBaseTest::class.java)

    init {
        LoggerFactory.getILoggerFactory().addAppenderToRootLogger(underTest)
    }


    @Test
    fun disabled_NothingGetsWrittenToLogWriter() {
        log.error("Any")

        assertThat(countCalls.get()).isEqualTo(0)
    }

}