package net.codinux.log.loki.quarkus;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class QuarkusLokiLoggerProcessor {

    private static final String FEATURE = "loki-logger";

    @BuildStep
    public FeatureBuildItem feature() {
        System.out.println("BUilding feature"); // TODO: remove again

        return new FeatureBuildItem(FEATURE);
    }
}
