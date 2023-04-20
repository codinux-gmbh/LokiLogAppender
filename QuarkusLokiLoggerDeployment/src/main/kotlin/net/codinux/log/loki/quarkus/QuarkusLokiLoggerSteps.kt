package net.codinux.log.loki.quarkus

import io.quarkus.deployment.annotations.BuildStep
import io.quarkus.deployment.annotations.ExecutionTime
import io.quarkus.deployment.annotations.Record
import io.quarkus.deployment.builditem.LogHandlerBuildItem
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig

class QuarkusLokiLoggerSteps {

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    fun setUpFormatter(recorder: QuarkusLokiLogAppenderRecorder, config: QuarkusLokiLogAppenderConfig): LogHandlerBuildItem =
        LogHandlerBuildItem(recorder.initializeLokiLogAppender(config))

}