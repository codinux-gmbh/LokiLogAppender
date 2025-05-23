package net.codinux.log.loki.util

import kotlin.test.Test
import kotlin.test.assertEquals

class LokiLabelEscaperTest {

    private val underTest = LokiLabelEscaper.Default


    @Test
    fun labelStartsWithNumber_GetsReplaced() {
        val result = underTest.escapeLabelName("1")

        assertEquals("_", result)
    }

    @Test
    fun numberInLabelNotInFirstPlace_DoesNotGetReplaced() {
        val result = underTest.escapeLabelName("MDC1")

        assertEquals("MDC1", result)
    }

    @Test
    fun multipleIllegalCharacters_AllGetReplaced() {
        val result = underTest.escapeLabelName("MDC1.%Liebe")

        assertEquals("MDC1__Liebe", result)
    }

}