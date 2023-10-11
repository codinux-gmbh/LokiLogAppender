package net.codinux.log.loki

import net.codinux.log.logback.LogbackAppenderBase
import net.codinux.log.logback.statelogger.LogbackStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig

open class LogbackLokiAppender(config: LokiLogAppenderConfig = LokiLogAppenderConfig())
    : LogbackAppenderBase(config.enabled, LokiLogWriter(config, LogbackStateLogger(config.stateLoggerName ?: LokiLogAppenderConfig.StateLoggerDefaultName))) {

        fun setTenantId(tenantId: String) {
            (config as? LokiLogAppenderConfig)?.tenantId = tenantId
        }
    }