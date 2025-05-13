package net.codinux.log.loki.quarkus;

import net.codinux.log.loki.JBossLoggingLokiAppender;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.mapper.QuarkusLokiConfigMapper;

public class QuarkusLokiLogAppender extends JBossLoggingLokiAppender {

    public QuarkusLokiLogAppender(QuarkusLokiLogAppenderConfig config) {
        super(QuarkusLokiConfigMapper.mapConfig(config));
    }

}
