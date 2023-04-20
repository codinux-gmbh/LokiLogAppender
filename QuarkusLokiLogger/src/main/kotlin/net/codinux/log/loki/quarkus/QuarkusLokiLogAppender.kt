package net.codinux.log.loki.quarkus

import net.codinux.log.LogAppenderConfig
import net.codinux.log.loki.LokiJBossLoggingAppender
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig

class QuarkusLokiLogAppender(quarkusLokiConfig: QuarkusLokiLogAppenderConfig) : LokiJBossLoggingAppender(mapConfig(quarkusLokiConfig)) {

    companion object {

        private fun mapConfig(config: QuarkusLokiLogAppenderConfig): LogAppenderConfig {

            return LogAppenderConfig()
        }

    }

}