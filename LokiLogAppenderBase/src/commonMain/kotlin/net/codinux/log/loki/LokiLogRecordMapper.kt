package net.codinux.log.loki

import net.codinux.log.LogRecordMapper
import net.codinux.log.config.LogAppenderFieldsConfig
import net.codinux.log.loki.util.LokiLabelEscaper

open class LokiLogRecordMapper(config: LogAppenderFieldsConfig) : LogRecordMapper(config, false, true) {

    protected open val labelEscaper = LokiLabelEscaper()


    override fun escapeDynamicLabelName(key: String) =
        labelEscaper.escapeLabelName(key)

}