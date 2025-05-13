package net.codinux.log.loki

import net.codinux.log.LogWriter
import net.codinux.log.config.LogAppenderConfig
import net.codinux.log.LogbackAppenderBase
import net.codinux.log.statelogger.LogbackStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.web.JavaHttpClientWebClient

open class LogbackLokiAppender(config: LokiLogAppenderConfig = LokiLogAppenderConfig())
    : LogbackAppenderBase(config) {

    override fun createLogWriter(config: LogAppenderConfig): LogWriter {
        val mappedConfig = config as LokiLogAppenderConfig
        val stateLogger = LogbackStateLogger(config.stateLoggerName ?: LokiLogAppenderConfig.StateLoggerDefaultName)
        val webClient = JavaHttpClientWebClient.of(mappedConfig, stateLogger)

        return LokiLogWriter(mappedConfig, stateLogger, webClient)
    }


    /*      Loki specific configuration     */

    fun setTenantId(tenantId: String) {
        (config as? LokiLogAppenderConfig)?.tenantId = tenantId
    }

}