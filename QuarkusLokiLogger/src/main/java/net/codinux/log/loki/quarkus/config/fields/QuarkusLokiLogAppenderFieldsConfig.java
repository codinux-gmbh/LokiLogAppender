package net.codinux.log.loki.quarkus.config.fields;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithName;
import net.codinux.log.loki.quarkus.config.fields.kubernetes.LokiKubernetesInfoConfig;

@ConfigGroup
public interface QuarkusLokiLogAppenderFieldsConfig {

    /**
     * Configure if log level should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("level")
    LogLevelConfig logLevel();

    /**
     * Configure if logger name should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("loggername")
    LoggerNameConfig loggerName();

    /**
     * Configure if logger class name should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("loggerclass")
    LoggerClassNameConfig loggerClassName();


    /**
     * Configure if thread name should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("threadname")
    ThreadNameConfig threadName();

    /**
     * Configure if stack trace should get logged as Label or StructuredMetadata and its field name.
     */
    StacktraceConfig stacktrace();

    /**
     * Configure if app name should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("app")
    AppNameConfig appName();

    /**
     * Configure if app version should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("version")
    AppVersionConfig appVersion();

    /**
     * Configure if job name should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("job")
    JobNameConfig jobName();


    /**
     * Configure if host name should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("hostname")
    HostNameConfig hostName();

    /**
     * Configure if host IP should get logged as Label or StructuredMetadata and its field name.
     */
    @WithName("hostip")
    HostIpConfig hostIp();


    /**
     * Configure if MDC should get logged as Label or StructuredMetadata and its field name.
     */
    MdcConfig mdc();

    /**
     * Configure if markers should get logged as Label or StructuredMetadata and its field name.
     */
    MarkerConfig marker();

    /**
     * Configure if NDC should get logged as Label or StructuredMetadata and its field name.
     */
    NdcConfig ndc();

    /**
     * Configure which Kubernetes values to include in log.
     */
    @WithName("kubernetes")
    LokiKubernetesInfoConfig kubernetesInfo();

}