package net.codinux.log.loki

import net.codinux.log.LogAppenderConfig
import net.codinux.log.jboss.JBossLoggingAppenderBase
import net.codinux.log.jboss.statelogger.JBossLoggingStateLogger
import net.codinux.log.loki.LokiLogAppenderConfig.StateLoggerDefaultName

open class LokiJBossLoggingAppender(config: LogAppenderConfig)
    : JBossLoggingAppenderBase(config.enabled, LokiLogWriter(config, JBossLoggingStateLogger(StateLoggerDefaultName)))