package net.codinux.log.loki.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import net.codinux.log.quarkus.config.QuarkusLogAppenderConfigBase;

import java.util.Optional;

@ConfigMapping(prefix = "quarkus.log.loki")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface QuarkusLokiLogAppenderConfig extends QuarkusLogAppenderConfigBase {

    /**
     * For multi-tenant Loki setups the tenant id to use
     */
    @WithName("tenantid")
    Optional<String> tenantId();

}
