package net.codinux.log.loki

import net.codinux.log.JBossLoggingAppenderBase
import net.codinux.log.statelogger.JBossLoggingStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig.Companion.StateLoggerDefaultName
import net.codinux.log.loki.web.JavaHttpClientWebClient
import net.codinux.log.loki.web.WebClient
import net.codinux.log.statelogger.AppenderStateLogger

open class JBossLoggingLokiAppender(
    config: LokiLogAppenderConfig,
    stateLogger: AppenderStateLogger = createStateLogger(config),
    webClient: WebClient = JavaHttpClientWebClient.of(config, stateLogger),
) : JBossLoggingAppenderBase(LokiLogWriter(config, stateLogger, webClient)) {

    companion object {
        fun createStateLogger(config: LokiLogAppenderConfig): AppenderStateLogger {
            return JBossLoggingStateLogger(config.stateLoggerName ?: StateLoggerDefaultName)
        }
    }


    // Java does not support default parameters
    constructor(config: LokiLogAppenderConfig) : this(config, createStateLogger(config))

}