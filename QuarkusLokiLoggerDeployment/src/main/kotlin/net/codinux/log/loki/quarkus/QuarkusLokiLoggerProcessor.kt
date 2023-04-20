package net.codinux.log.loki.quarkus

import io.quarkus.deployment.annotations.BuildStep
import io.quarkus.deployment.builditem.FeatureBuildItem

class QuarkusLokiLoggerProcessor {

    companion object {
        private const val Feature = "loki-logger"
    }


    @BuildStep
    fun feature() = FeatureBuildItem(Feature)

}