package net.codinux.log.loki

import net.codinux.log.LogAppenderConfig
import net.codinux.log.jboss.JBossLoggingAppenderBase

open class LokiJBossLoggingAppender(config: LogAppenderConfig) : JBossLoggingAppenderBase(config.enabled, LokiLogWriter(config))