package net.codinux.log.loki.quarkus;

import net.codinux.log.loki.JBossLoggingLokiAppender;
import net.codinux.log.loki.config.LokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.mapper.QuarkusLokiConfigMapper;
import net.codinux.log.loki.web.JavaHttpClientWebClient;
import net.codinux.log.loki.web.WebClient;
import net.codinux.log.statelogger.AppenderStateLogger;
import net.codinux.log.statelogger.JBossLoggingStateLogger;

public class QuarkusLokiLogAppender extends JBossLoggingLokiAppender {

    public static QuarkusLokiLogAppender of(QuarkusLokiLogAppenderConfig config) {
        LokiLogAppenderConfig mappedConfig = QuarkusLokiConfigMapper.mapConfig(config);
        AppenderStateLogger stateLogger = stateLogger(config);

        return new QuarkusLokiLogAppender(mappedConfig, stateLogger, webClient(mappedConfig, stateLogger));
    }

    private static AppenderStateLogger stateLogger(QuarkusLokiLogAppenderConfig config) {
        return new JBossLoggingStateLogger(config.stateLoggerName().orElse(LokiLogAppenderConfig.StateLoggerDefaultName));
    }

    private static WebClient webClient(LokiLogAppenderConfig config, AppenderStateLogger stateLogger) {
        return JavaHttpClientWebClient.Companion.of(config, stateLogger);
    }


    public QuarkusLokiLogAppender(QuarkusLokiLogAppenderConfig config) {
        this(QuarkusLokiConfigMapper.mapConfig(config), stateLogger(config));
    }

    public QuarkusLokiLogAppender(LokiLogAppenderConfig config, AppenderStateLogger stateLogger) {
        this(config, stateLogger, webClient(config, stateLogger));
    }

    public QuarkusLokiLogAppender(LokiLogAppenderConfig config, AppenderStateLogger stateLogger, WebClient webClient) {
        super(config, stateLogger, webClient);
    }

}
