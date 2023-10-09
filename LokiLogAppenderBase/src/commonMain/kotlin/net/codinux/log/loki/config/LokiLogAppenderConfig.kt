package net.codinux.log.loki.config

import net.codinux.log.config.LogAppenderConfig
import net.codinux.log.config.LogAppenderFieldsConfig
import net.codinux.log.config.WriterConfig

open class LokiLogAppenderConfig(
    enabled: Boolean = EnabledDefaultValue,

    writer: WriterConfig = WriterConfig(),

    fields: LogAppenderFieldsConfig = LogAppenderFieldsConfig(),

    open var tenantId: String? = null,

    stateLoggerName: String? = StateLoggerNotSet
) : LogAppenderConfig(enabled, writer, fields, stateLoggerName) {

    companion object {
        const val StateLoggerDefaultName = "net.codinux.log.loki.LokiStateLogger"
    }

}