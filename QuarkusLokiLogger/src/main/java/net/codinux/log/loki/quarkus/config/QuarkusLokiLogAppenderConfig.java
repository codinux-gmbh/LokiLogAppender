package net.codinux.log.loki.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import net.codinux.log.config.quarkus.QuarkusLogAppenderConfigBase;

@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = "log.loki")
public class QuarkusLokiLogAppenderConfig extends QuarkusLogAppenderConfigBase {

}
