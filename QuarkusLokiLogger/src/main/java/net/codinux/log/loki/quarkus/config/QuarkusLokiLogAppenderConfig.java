package net.codinux.log.loki.quarkus.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import net.codinux.log.config.quarkus.QuarkusLogAppenderConfigBase;

@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = "log.loki")
public class QuarkusLokiLogAppenderConfig extends QuarkusLogAppenderConfigBase {

    /**
     * For multi-tenant Loki setups the tenant id to use
     */
    @ConfigItem(name = "tenantid", defaultValue = "null")
    public String tenantId = null;

}
