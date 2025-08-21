package net.codinux.log.loki.config

import net.codinux.log.auth.Authentication
import net.codinux.log.auth.BasicAuthAuthentication
import net.codinux.log.config.WriterConfig

open class LokiLogAppenderConfig(
    open var enabled: Boolean = EnabledDefaultValue,

    open var lokiBaseUrl: String = BaseUrlNotSet,

    open var username: String? = UsernameNotSet,
    open var password: String? = PasswordNotSet,

    open var tenantId: String? = null,

    open var fields: LogFieldsConfig = LogFieldsConfig(),

    open var writer: WriterConfig = WriterConfig(),

    open var stateLoggerName: String? = StateLoggerNotSet
) {

    companion object {
        const val EnabledDefaultValue = true
        const val EnabledDefaultValueString = EnabledDefaultValue.toString()

        const val BaseUrlNotSet = "null"

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