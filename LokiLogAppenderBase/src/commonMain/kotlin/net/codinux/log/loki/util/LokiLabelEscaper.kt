package net.codinux.log.loki.util

import net.codinux.log.config.KubernetesFieldsConfig
import net.codinux.log.config.LogAppenderConfig

class LokiLabelEscaper {

    companion object {

        // Loki label names must match the regex [a-zA-Z_:][a-zA-Z0-9_:]*.
        // But i removed the colon as "The colons are reserved for user defined recording rules. They should not be used by exporters or direct instrumentation."
        private val IllegalLabelCharactersRegex = Regex("[^a-zA-Z_][^a-zA-Z0-9_]*")

    }

    fun escapeLabelNames(config: LogAppenderConfig): LogAppenderConfig {
        val fields = config.fields

        fields.logLevelFieldName = escapeLabelName(fields.logLevelFieldName)
        fields.loggerNameFieldName = escapeLabelName(fields.loggerNameFieldName)
        fields.loggerClassNameFieldName = escapeLabelName(fields.loggerClassNameFieldName)
        fields.threadNameFieldName = escapeLabelName(fields.threadNameFieldName)

        fields.hostNameFieldName = escapeLabelName(fields.hostNameFieldName)
        fields.hostIpFieldName = escapeLabelName(fields.hostIpFieldName)
        fields.appNameFieldName = escapeLabelName(fields.appNameFieldName)
        fields.appVersionFieldName = escapeLabelName(fields.appVersionFieldName)
        fields.stacktraceFieldName = escapeLabelName(fields.stacktraceFieldName)

        fields.mdcKeysPrefix = determinePrefix(fields.mdcKeysPrefix)

        fields.markerFieldName = escapeLabelName(fields.markerFieldName)
        fields.ndcFieldName = escapeLabelName(fields.ndcFieldName)

        fields.kubernetesFieldsPrefix = determinePrefix(fields.kubernetesFieldsPrefix)
        escapeKubernetesFieldsLabelNames(fields.kubernetesFields)

        return config
    }

    private fun escapeKubernetesFieldsLabelNames(fields: KubernetesFieldsConfig) {
        fields.namespaceFieldName = escapeLabelName(fields.namespaceFieldName)

        fields.podNameFieldName = escapeLabelName(fields.podNameFieldName)
        fields.containerNameFieldName = escapeLabelName(fields.containerNameFieldName)
        fields.imageNameFieldName = escapeLabelName(fields.imageNameFieldName)

        fields.nodeNameFieldName = escapeLabelName(fields.nodeNameFieldName)
        fields.nodeIpFieldName = escapeLabelName(fields.nodeIpFieldName)
        fields.podIpFieldName = escapeLabelName(fields.podIpFieldName)

        fields.startTimeFieldName = escapeLabelName(fields.startTimeFieldName)
        fields.restartCountFieldName = escapeLabelName(fields.restartCountFieldName)

        fields.podUidFieldName = escapeLabelName(fields.podUidFieldName)
        fields.containerIdFieldName = escapeLabelName(fields.containerIdFieldName)
        fields.imageIdFieldName = escapeLabelName(fields.imageIdFieldName)

        fields.labelsPrefix = determinePrefix(fields.labelsPrefix)
        fields.annotationsPrefix = determinePrefix(fields.annotationsPrefix)
    }

    /**
     * It may contain ASCII letters and digits, as well as underscores and colons. It must match the regex [a-zA-Z_:][a-zA-Z0-9_:]*.
     *
     * Note: The colons are reserved for user defined recording rules. They should not be used by exporters or direct instrumentation.
     *
     * (And at least for Prometheus labels may not start with an underscore.)
     *
     * Label values may contain any Unicode characters.
     *
     * A label with an empty label value is considered equivalent to a label that does not exist.
     *
     * And:
     *
     * Labels are the index to Loki’s log data. They are used to find the compressed log content, which is stored separately as chunks.
     * Every unique combination of label and values defines a stream, and logs for a stream are batched up, compressed, and stored as chunks.
     *
     * For Loki to be efficient and cost-effective, we have to use labels responsibly.
     *
     * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
     *
     *
     * Therefore:
     * Do not use labels to store dimensions with high cardinality (many different label values), such as user IDs, email addresses, or other unbounded sets of values.
     * (https://prometheus.io/docs/practices/naming/)
     *
     * or:
     * Imagine now if you set a label for ip. Not only does every request from a user become a unique stream. Every request with a
     * different action or status_code from the same user will get its own stream.
     *
     *
     * Doing some quick math, if there are maybe four common actions (GET, PUT, POST, DELETE) and maybe four common status codes
     * (although there could be more than four!), this would be 16 streams and 16 separate chunks. Now multiply this by every user
     * if we use a label for ip. You can quickly have thousands or tens of thousands of streams.
     *
     * This is high cardinality. This can kill Loki.
     *
     * High cardinality causes Loki to build a huge index (read: $$$$) and to flush thousands of tiny chunks to the object store (read: slow).
     * Loki currently performs very poorly in this configuration and will be the least cost-effective and least fun to run and use.
     *
     * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
     *
     *
     * As we see people using Loki who are accustomed to other index-heavy solutions (..)
     * Large indexes are complicated and expensive. Often a full-text index of your log data is the same size or bigger than the log data itself.
     * To query your log data, you need this index loaded, and for performance, it should probably be in memory. This is difficult to scale, and
     * as you ingest more logs, your index gets larger quickly.
     *
     * Now let’s talk about Loki, where the index is typically an order of magnitude smaller than your ingested log volume. So if you are doing
     * a good job of keeping your streams and stream churn to a minimum, the index grows very slowly compared to the ingested logs.
     *
     * When using Loki, you may need to forget what you know and look to see how the problem can be solved differently with parallelization.
     * Loki’s superpower is breaking up queries into small pieces and dispatching them in parallel so that you can query huge amounts of log data in small amounts of time.
     *
     * (https://grafana.com/docs/loki/latest/fundamentals/labels/)
     */
    private fun escapeLabelName(fieldName: String): String {
        return fieldName.replace(IllegalLabelCharactersRegex, "_")
    }

    private fun determinePrefix(prefix: String?): String =
        if (prefix.isNullOrBlank()) ""
        else if (prefix.endsWith('.')) prefix.substring(0, prefix.length - 2) + "_" // in Loki prefixes get separated by '_', in ElasticSearch by '.'
        else if (prefix.endsWith('_')) prefix
        else prefix + "_"

}