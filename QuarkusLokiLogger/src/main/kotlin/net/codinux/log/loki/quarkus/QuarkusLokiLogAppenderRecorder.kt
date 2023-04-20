package net.codinux.log.loki.quarkus

import io.quarkus.runtime.RuntimeValue
import io.quarkus.runtime.annotations.Recorder
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig
import java.util.Optional
import java.util.logging.Handler

@Recorder
class QuarkusLokiLogAppenderRecorder {

    fun initializeLokiLogAppender(config: QuarkusLokiLogAppenderConfig): RuntimeValue<Optional<Handler>> {
        if (config.enable == false) {
            return RuntimeValue(Optional.empty())
        }

        if (config.lokiHost.isNullOrBlank()) {
            throw IllegalArgumentException("If loki-logger is enabled, then host value with the URL pointing to your Loki instance must be configured")
        }

        return RuntimeValue(Optional.of(QuarkusLokiLogAppender(config)))
    }

}