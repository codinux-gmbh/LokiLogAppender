package net.codinux.log.loki

import net.codinux.log.config.LogAppenderConfig
import net.codinux.log.LogbackAppenderBase
import net.codinux.log.statelogger.LogbackStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig

open class LogbackLokiAppender(config: LokiLogAppenderConfig = LokiLogAppenderConfig())
    : LogbackAppenderBase(config) {

    override fun createLogWriter(config: LogAppenderConfig) =
        LokiLogWriter(config as LokiLogAppenderConfig, LogbackStateLogger(config.stateLoggerName ?: LokiLogAppenderConfig.StateLoggerDefaultName))


    /*      Loki specific configuration     */

    fun setTenantId(tenantId: String) {
        (config as? LokiLogAppenderConfig)?.tenantId = tenantId
    }

}