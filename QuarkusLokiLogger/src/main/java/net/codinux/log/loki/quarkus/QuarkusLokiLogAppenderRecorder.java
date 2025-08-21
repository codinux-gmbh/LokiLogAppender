package net.codinux.log.loki.quarkus;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.logging.Handler;

@Recorder
public class QuarkusLokiLogAppenderRecorder {

    private final RuntimeValue<QuarkusLokiLogAppenderConfig> configValue;

    @Inject
    public QuarkusLokiLogAppenderRecorder(RuntimeValue<QuarkusLokiLogAppenderConfig> config) {
        this.configValue = config;
    }


    public RuntimeValue<Optional<Handler>> initializeLokiLogAppender() {
        QuarkusLokiLogAppenderConfig config = configValue.getValue();

        if (config.enable() == false) {
            return new RuntimeValue(Optional.empty());
        }

        if (config.lokiBaseUrl() == null || config.lokiBaseUrl().isBlank()) {
            throw new IllegalArgumentException("If loki-logger is enabled, then baseUrl value with the base URL pointing to your Loki instance must be configured");
        }

        return new RuntimeValue(Optional.of(new QuarkusLokiLogAppender(config)));
    }

}
