package net.codinux.log.loki.serialization

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.codinux.log.loki.model.LogStream
import net.dankito.datetime.LocalDateTime
import kotlin.test.Test

class LogStreamEntrySerializerTest {

    companion object {
        private val timestamp = LocalDateTime(2015, 10, 21, 5, 19, 37).toInstantAtUtc()
        private val timestampIsoString = timestamp.isoString

        private const val logLine = "Test message"
    }

    private val json = Json { prettyPrint = true }


    @Test
    fun serializeStream() {
        val stream = LogStream().apply {
            stream.putAll(mutableMapOf("level" to "INFO", "namespace" to "TeamA"))
            set(timestampIsoString, logLine)
        }

        val result = serialize(stream)


        assertResult(result, """
            {
                "stream": {
                    "level": "INFO",
                    "namespace": "TeamA"
                },
                "values": [
                    [
                        "$timestampIsoString",
                        "$logLine"
                    ]
                ]
            }
        """.trimIndent())
    }

    @Test
    fun serializeStructuredMetadata() {
        val stream = LogStream().apply {
            set(timestampIsoString, logLine)
            structuredMetadata.putAll(mapOf(
                "pod" to "SomePod-123",
                "logger" to "net.codinux.log.loki.LokiLogger"
            ))
        }

        val result = serialize(stream)


        assertResult(result, """
            {
                "values": [
                    [
                        "$timestampIsoString",
                        "$logLine",
                        {
                            "pod": "SomePod-123",
                            "logger": "net.codinux.log.loki.LokiLogger"
                        }
                    ]
                ]
            }""")
    }


    private fun serialize(stream: LogStream) = json.encodeToString(stream)

    private fun assertResult(result: String, expected: String) {
        assertThat(result).isEqualTo(expected.trimIndent())
    }

}