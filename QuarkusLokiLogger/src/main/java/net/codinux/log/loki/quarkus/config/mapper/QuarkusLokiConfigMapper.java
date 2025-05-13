package net.codinux.log.loki.quarkus.config.mapper;

import net.codinux.log.loki.config.LokiLogAppenderConfig;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;
import net.codinux.log.quarkus.config.mapper.QuarkusConfigMapper;

public class QuarkusLokiConfigMapper {

    public static LokiLogAppenderConfig mapConfig(QuarkusLokiLogAppenderConfig config) {
        LokiLogAppenderConfig mappedConfig = new LokiLogAppenderConfig();

        // map common fields
        QuarkusConfigMapper.mapConfigTo(config, mappedConfig);

        // map Loki config specific fields
        mappedConfig.setTenantId(QuarkusConfigMapper.mapNullableString(config.tenantId()));

        return mappedConfig;
    }

}