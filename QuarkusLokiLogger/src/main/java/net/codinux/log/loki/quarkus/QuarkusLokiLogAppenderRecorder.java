package net.codinux.log.loki.quarkus;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;
import java.util.Optional;
import java.util.logging.Handler;

@Recorder
public class QuarkusLokiLogAppenderRecorder {

    public RuntimeValue<Optional<Handler>> initializeLokiLogAppender(QuarkusLokiLogAppenderConfig config) {
        if (config.enable() == false) {
            return new RuntimeValue(Optional.empty());
        }

        if (config.hostUrl() == null || config.hostUrl().isBlank()) {
            throw new IllegalArgumentException("If loki-logger is enabled, then host value with the URL pointing to your Loki instance must be configured");
        }

        return new RuntimeValue(Optional.of(new QuarkusLokiLogAppender(config)));
    }
}
