package net.codinux.log.loki

import net.codinux.log.JBossLoggingAppenderBase
import net.codinux.log.statelogger.JBossLoggingStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig.Companion.StateLoggerDefaultName

open class JBossLoggingLokiAppender(config: LokiLogAppenderConfig)
    : JBossLoggingAppenderBase(LokiLogWriter(config, JBossLoggingStateLogger(config.stateLoggerName ?: StateLoggerDefaultName)))