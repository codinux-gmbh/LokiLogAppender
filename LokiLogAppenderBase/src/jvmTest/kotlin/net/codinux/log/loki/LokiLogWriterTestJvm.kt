package net.codinux.log.loki

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.codinux.log.config.LogAppenderFieldsConfig
import net.codinux.log.config.WriterConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.web.KtorWebClient
import net.codinux.log.statelogger.StdOutStateLogger
import net.dankito.datetime.Instant
import org.junit.jupiter.api.Test

class LokiLogWriterTestJvm {

    private val config = LokiLogAppenderConfig(
        writer = WriterConfig("http://localhost:3100"),
        fields = LogAppenderFieldsConfig(
            includeLoggerClassName = true,
            includeAppName = true,
            appName = "Liebestest"
        )
    )

    private val underTest = LokiLogWriter(config, StdOutStateLogger(), KtorWebClient.of(config, StdOutStateLogger()))

    @Test
    fun writeLogs() = runBlocking {
        IntRange(0, 100).forEach { index ->
            underTest.writeRecord(
                getTimestamp(),
                "INFO",
                "Test message ${index.toString().padStart(3, '0')}",
                "net.codinux.LokiTest",
                "main",
                exception = Exception("Just a test, no animals have been harmed"),
                mdc = mapOf("MDC1" to "Stasi", "MDC2" to "Wuerd ich gerne knutschen")
            )

            delay(50)
        }
    }

    @Test
    fun messageContainsQuotes() = runBlocking {
        underTest.writeRecord(
            getTimestamp(),
            "INFO",
            """RESTEASY002142: Multiple resource methods match request "GET /favicon-finder". Selecting one. Matching methods: [public javax.ws.rs.core.Response ...)]""",
            "net.codinux.LokiTest",
            "main"
        )

        delay(5000)

        // TODO: add a assert to assert that HTTP 204 got returned instead of 400
    }

    @Test
    fun messageContainsControlCharacters() = runBlocking {
        underTest.writeRecord(
            getTimestamp(),
            "INFO",
            "RESTEASY002142: Multiple resource methods match request GET /favicon-finder. Selecting one. Matching methods: [\npublic javax.ws.rs.core.Response\r net.dankito.utils.favicon.rest.FaviconFinderResource.findFavicons(java.lang.String,net.dankito.utils.favicon.rest.model.SizeSorting),\t public java.lang.String net.dankito.utils.favicon.rest.FaviconFinderResource.findFaviconsHtml(java.lang.String,net.dankito.utils.favicon.rest.model.SizeSorting)]",
            "net.codinux.LokiTest",
            "main"
        )

        delay(500)

        // TODO: add a assert to assert that HTTP 204 got returned instead of 400
    }


    private fun getTimestamp() = Instant.now()

}