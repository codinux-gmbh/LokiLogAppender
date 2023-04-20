package net.codinux.log.loki.quarkus;

import net.codinux.log.LogAppenderConfig;
import net.codinux.log.loki.LokiJBossLoggingAppender;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;

public class QuarkusLokiLogAppender extends LokiJBossLoggingAppender {

    public QuarkusLokiLogAppender(QuarkusLokiLogAppenderConfig config) {
        super(mapConfig(config));
    }

    private static LogAppenderConfig mapConfig(QuarkusLokiLogAppenderConfig config) {
        LogAppenderConfig mappedConfig = new LogAppenderConfig();

        mappedConfig.setEnabled(config.enable);
        mappedConfig.setHost(config.lokiHost);

        return mappedConfig;
    }
}
