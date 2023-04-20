package net.codinux.log.loki.quarkus.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = "log.loki")
public class QuarkusLokiLogAppenderConfig {

    /**
     * If logging to Loki should be enabled or not.
     */
    @ConfigItem(defaultValue = "true") // LogAppenderConfig.EnabledDefaultValueString) // TODO
    public boolean enable;

    /**
     * Loki host.
     */
    @ConfigItem(name = "host")
    public String lokiHost;

}
