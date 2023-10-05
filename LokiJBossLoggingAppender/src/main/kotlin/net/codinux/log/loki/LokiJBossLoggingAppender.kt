package net.codinux.log.loki

import net.codinux.log.jboss.JBossLoggingAppenderBase
import net.codinux.log.jboss.statelogger.JBossLoggingStateLogger
import net.codinux.log.loki.LokiLogAppenderConfig.Companion.StateLoggerDefaultName

open class LokiJBossLoggingAppender(config: LokiLogAppenderConfig)
    : JBossLoggingAppenderBase(config.enabled, LokiLogWriter(config, JBossLoggingStateLogger(StateLoggerDefaultName)))