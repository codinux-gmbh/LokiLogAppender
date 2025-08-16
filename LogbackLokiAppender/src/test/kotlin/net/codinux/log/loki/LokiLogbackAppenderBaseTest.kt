package net.codinux.log.loki

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import io.mockk.*
import net.codinux.log.LogWriter
import net.codinux.log.LogbackAppenderBase
import net.codinux.log.addAppenderToRootLogger
import net.codinux.log.config.CostlyFieldsConfig
import net.dankito.datetime.Instant
import net.dankito.datetime.toJavaInstant
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class LokiLogbackAppenderBaseTest {

    private val logWriterMock = mockk<LogWriter> {
        every { this@mockk.isEnabled } returns true
        every { this@mockk.costlyFields } returns CostlyFieldsConfig.All
    }

    private val underTest = object : LogbackAppenderBase() {
        override fun createLogWriter() = logWriterMock
    }.apply {
        this.start()
    }

    private val log = LoggerFactory.getLogger(LokiLogbackAppenderBaseTest::class.java)

    init {
        LoggerFactory.getILoggerFactory().addAppenderToRootLogger(underTest)
    }


    @Test
    fun callToSlf4jLoggerCallsLogWriter() {
        val message = "Just a test, no animals have been harmed"
        val exception = RuntimeException("As i said, just a test")

        val capturedInstant = slot<Instant>()
        val capturedLevel = slot<String>()
        val capturedMessage = slot<String>()
        val capturedException = slot<Throwable>()
        every { logWriterMock.writeRecord(capture(capturedInstant), capture(capturedLevel), capture(capturedMessage), any(), any(), capture(capturedException), any(), any(), any()) } just runs


        log.error(message, exception)


        assertThat(capturedMessage.captured).isEqualTo(message)
        assertThat(capturedException.captured).isEqualTo(exception)
        assertThat(capturedLevel.captured).isEqualTo("ERROR")
        assertThat(capturedInstant.captured.toJavaInstant()).isBetween(java.time.Instant.now().minusSeconds(1), java.time.Instant.now().plusSeconds(1))
    }

}