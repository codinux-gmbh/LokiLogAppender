package net.codinux.log.loki

import net.codinux.log.jboss.JBossLoggingAppenderBase
import net.codinux.log.jboss.statelogger.JBossLoggingStateLogger
import net.codinux.log.loki.config.LokiLogAppenderConfig
import net.codinux.log.loki.config.LokiLogAppenderConfig.Companion.StateLoggerDefaultName

open class LokiJBossLoggingAppender(config: LokiLogAppenderConfig)
    : JBossLoggingAppenderBase(config.enabled, LokiLogWriter(config, JBossLoggingStateLogger(config.stateLoggerName ?: StateLoggerDefaultName)))