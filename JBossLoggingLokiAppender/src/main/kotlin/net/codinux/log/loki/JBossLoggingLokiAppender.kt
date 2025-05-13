package net.codinux.log.loki

import net.codinux.log.JBossLoggingAppenderBase
import net.codinux.log.statelogger.JBossLoggingStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig.Companion.StateLoggerDefaultName
import net.codinux.log.loki.web.KtorWebClient
import net.codinux.log.loki.web.WebClient
import net.codinux.log.statelogger.AppenderStateLogger

open class JBossLoggingLokiAppender(
    config: LokiLogAppenderConfig,
    stateLogger: AppenderStateLogger = JBossLoggingStateLogger(config.stateLoggerName ?: StateLoggerDefaultName),
    webClient: WebClient = KtorWebClient.of(config, stateLogger),
) : JBossLoggingAppenderBase(LokiLogWriter(config, stateLogger, webClient)) {

    // Java does not support default parameters
    constructor(config: LokiLogAppenderConfig) : this(config, JBossLoggingStateLogger(config.stateLoggerName ?: StateLoggerDefaultName))

}