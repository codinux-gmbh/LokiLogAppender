package net.codinux.log.loki

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import net.codinux.log.LogRecord
import net.codinux.log.LogAppenderConfig
import org.junit.jupiter.api.Test

class LokiLogWriterTest {

    private val underTest = LokiLogWriter(
        LogAppenderConfig(host = "http://localhost:3100", includeLoggerClassName = true, includeAppName = true, appName = "Liebestest")
    )

    @Test
    fun writeLogs() = runBlocking {
        IntRange(0, 100).forEach { index ->
            underTest.writeRecord(LogRecord(
                "Test message ${index.toString().padStart(3, '0')}",
                Clock.System.now(),
                "INFO",
                "net.codinux.Liebe",
                "main",
                exception = Exception("Just a test, no animals have been harmed"),
                mdc = mapOf("MDC1" to "Stasi", "MDC2" to "Wuerd ich gerne knutschen")
            ))
            delay(50)
        }
    }
}