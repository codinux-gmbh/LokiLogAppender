package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithName;
import net.codinux.log.loki.quarkus.config.fields.kubernetes.LokiKubernetesInfoConfig;
import net.codinux.log.quarkus.config.fields.QuarkusLogAppenderFieldsConfig;

public interface QuarkusLokiLogAppenderFieldsConfig extends QuarkusLogAppenderFieldsConfig {

    /**
     * Config for the app name.
     */
    @Override
    @WithName("app")
    LokiAppNameConfig appName();

    /**
     * Config for the job name.
     */
    @Override
    @WithName("job")
    LokiJobNameConfig jobName();

    @Override
    LokiMdcConfig mdc();


    /**
     * Configure which Kubernetes values to include in log.
     */
    @Override
    @WithName("kubernetes")
    LokiKubernetesInfoConfig kubernetesInfo();

}