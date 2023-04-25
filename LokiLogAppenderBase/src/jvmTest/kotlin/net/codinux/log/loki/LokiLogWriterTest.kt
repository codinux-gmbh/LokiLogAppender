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
                "net.codinux.LokiTest",
                "main",
                exception = Exception("Just a test, no animals have been harmed"),
                mdc = mapOf("MDC1" to "Stasi", "MDC2" to "Wuerd ich gerne knutschen")
            ))
            delay(50)
        }
    }

    @Test
    fun messageContainsQuotes() = runBlocking {
        underTest.writeRecord(LogRecord(
            """RESTEASY002142: Multiple resource methods match request "GET /favicon-finder". Selecting one. Matching methods: [public javax.ws.rs.core.Response net.dankito.utils.favicon.rest.FaviconFinderResource.findFavicons(java.lang.String,net.dankito.utils.favicon.rest.model.SizeSorting), public java.lang.String net.dankito.utils.favicon.rest.FaviconFinderResource.findFaviconsHtml(java.lang.String,net.dankito.utils.favicon.rest.model.SizeSorting)]""",
            Clock.System.now(),
            "INFO",
            "net.codinux.LokiTest",
            "main"
        ))

        delay(1000)

        // TODO: add a assert to assert that HTTP 204 got returned instead of 400
    }
}