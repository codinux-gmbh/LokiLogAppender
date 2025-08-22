package net.codinux.log.loki

import net.codinux.log.LogWriter
import net.codinux.log.LogbackAppenderBase
import net.codinux.log.config.WriterConfig
import net.codinux.log.loki.config.fields.LogFieldsConfig
import net.codinux.log.statelogger.LogbackStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.web.JavaHttpClientWebClient

open class LogbackLokiAppender(protected open val config: LokiLogAppenderConfig = LokiLogAppenderConfig())
    : LogbackAppenderBase() {

    override fun createLogWriter(): LogWriter {
        val stateLogger = LogbackStateLogger(config.stateLoggerName ?: LokiLogAppenderConfig.StateLoggerDefaultName)
        val webClient = JavaHttpClientWebClient.of(config, stateLogger)

        return LokiLogWriter(config, stateLogger, webClient)
    }


    open fun setEnabled(enabled: Boolean) {
        config.enabled = enabled
    }


    open fun setLokiBaseUrl(baseUrl: String) {
        config.lokiBaseUrl = baseUrl
    }

    open fun setUsername(username: String?) {
        config.username = username
    }

    open fun setPassword(password: String?) {
        config.password = password
    }


    open fun setTenantId(tenantId: String) {
        config.tenantId = tenantId
    }

    open fun setStateLoggerName(stateLoggerName: String?) {
        config.stateLoggerName = stateLoggerName
    }


    open fun getFields(): LogFieldsConfig = config.fields

    open fun setFields(fields: LogFieldsConfig) {
        config.fields = fields
    }

    open fun getWriter(): WriterConfig = config.writer

    open fun setWriter(writer: WriterConfig) {
        config.writer = writer
    }

}