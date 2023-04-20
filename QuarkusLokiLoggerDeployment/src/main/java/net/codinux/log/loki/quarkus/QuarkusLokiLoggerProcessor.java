package net.codinux.log.loki.quarkus;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class QuarkusLokiLoggerProcessor {

    private static final String FEATURE = "loki-logger";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
