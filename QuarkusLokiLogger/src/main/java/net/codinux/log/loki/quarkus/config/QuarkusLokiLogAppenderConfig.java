package net.codinux.log.loki.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.loki.quarkus.config.fields.QuarkusLokiLogAppenderFieldsConfig;
import net.codinux.log.quarkus.config.QuarkusWriterConfig;

import java.util.Optional;

@ConfigMapping(prefix = "quarkus.log.loki")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface QuarkusLokiLogAppenderConfig {

    /**
     * If logging to Loki should be enabled or not.
     *
     * Defaults to {@code true}.
     */
    @WithDefault("true")
    boolean enable();

    /**
     * The base URL under which the Loki instance can be reached without the path ('/loki/api/v1'). E.g.
     * http://localhost:3100
     * http://loki.monitoring:3100
     */
    @WithName("baseUrl")
    String lokiBaseUrl();

    /**
     * In case Lo
     */
    QuarkusAuthenticationConfig authentication();

    /**
     * For multi-tenant Loki setups the tenant id to use
     */
    @WithName("tenantid")
    Optional<String> tenantId();

    /**
     * Configuration of logged fields.
     */
    @WithName("field")
    QuarkusLokiLogAppenderFieldsConfig fields();


    /**
     * Configure batch and network settings.
     */
    QuarkusWriterConfig writer();

    /**
     * The logger name under which the internal state and errors get logged.
     */
    Optional<String> stateLoggerName();

}
