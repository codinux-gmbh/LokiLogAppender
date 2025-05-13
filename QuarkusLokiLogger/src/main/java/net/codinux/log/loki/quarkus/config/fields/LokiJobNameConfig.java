package net.codinux.log.loki.quarkus.config.fields;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import net.codinux.log.quarkus.config.fields.JobNameConfig;

public interface LokiJobNameConfig extends JobNameConfig {

    /**
     * If the job name field should be included as tag in Loki index.
     *
     * Defaults to {@code true} to distinguish it from other log collectors.
     *
     * The logged job name can be configured with {@link LokiJobNameConfig#jobName()} and defaults to {@code "LokiLogger"}.
     */
    @WithDefault(LokiFieldsConfig.IncludeJobNameDefaultValueString)
    boolean include();

    /**
     * The value of the job name tag. So that logs written with LokiLogger can be
     * differentiated from jobs of log collectors like fluentd and K8sLogCollector.
     *
     * Only gets written if {@link LokiJobNameConfig#include()} is set to {@code true}.
     *
     * Defaults to {@code "LokiLogger"}.
     */
    @WithName("name")
    @WithDefault(LokiFieldsConfig.JobNameDefaultValue)
    String jobName();

}