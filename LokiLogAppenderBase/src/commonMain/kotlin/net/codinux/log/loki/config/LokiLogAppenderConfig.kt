package net.codinux.log.loki.config

import net.codinux.log.auth.Authentication
import net.codinux.log.auth.BasicAuthAuthentication
import net.codinux.log.config.LogAppenderFieldsConfig
import net.codinux.log.config.WriterConfig

open class LokiLogAppenderConfig(
    open var enabled: Boolean = EnabledDefaultValue,

    open var hostUrl: String = HostUrlNotSet,

    open var username: String? = UsernameNotSet,
    open var password: String? = PasswordNotSet,

    open var tenantId: String? = null,

    open var fields: LogAppenderFieldsConfig = LogAppenderFieldsConfig(),

    open var writer: WriterConfig = WriterConfig(),

    open var stateLoggerName: String? = StateLoggerNotSet
) {

    companion object {
        const val EnabledDefaultValue = true
        const val EnabledDefaultValueString = EnabledDefaultValue.toString()

        const val HostUrlNotSet = "null"

        val UsernameNotSet: String? = null
        const val UsernameNotSetString = "null"

        val PasswordNotSet: String? = null
        const val PasswordNotSetString = "null"

        val StateLoggerNotSet: String? = null
        const val StateLoggerNotSetString = "null"
        const val StateLoggerDefaultName = "net.codinux.log.loki.LokiStateLogger"
    }


    open fun getAuthentication(): Authentication? =
        if (username != UsernameNotSet && password != PasswordNotSet) {
            BasicAuthAuthentication(username!!, password!!)
        } else {
            null
        }

}