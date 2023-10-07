package net.codinux.log.loki.config

import net.codinux.log.config.LogAppenderConfig

open class LokiLogAppenderConfig : LogAppenderConfig() {

    companion object {
        const val StateLoggerDefaultName = "net.codinux.log.loki.LokiStateLogger"
    }

    open var tenantId: String? = null

}