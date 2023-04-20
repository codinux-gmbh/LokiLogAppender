package net.codinux.log.loki.quarkus.config

import io.quarkus.runtime.annotations.ConfigItem
import io.quarkus.runtime.annotations.ConfigPhase
import io.quarkus.runtime.annotations.ConfigRoot
import net.codinux.log.LogAppenderConfig

@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = "log.loki")
class QuarkusLokiLogAppenderConfig {

    /**
     * If logging to Loki should be enabled or not.
     */
    @ConfigItem(defaultValue = "true") // LogAppenderConfig.EnabledDefaultValueString) // TODO
    var enable = false

    /**
     * Loki host.
     */
    @ConfigItem(name = "host")
    var lokiHost: String? = null

}