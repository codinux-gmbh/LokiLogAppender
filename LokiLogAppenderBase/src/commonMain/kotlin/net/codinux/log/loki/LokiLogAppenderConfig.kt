package net.codinux.log.loki

import net.codinux.log.LogAppenderConfig

open class LokiLogAppenderConfig : LogAppenderConfig() {

    companion object {
        const val StateLoggerDefaultName = "net.codinux.log.loki.LokiStateLogger"
    }

    open var tenantId: String? = null

}