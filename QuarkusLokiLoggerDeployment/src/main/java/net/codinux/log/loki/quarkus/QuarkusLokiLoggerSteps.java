package net.codinux.log.loki.quarkus;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;

import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;

public class QuarkusLokiLoggerSteps {

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public LogHandlerBuildItem setUpLogAppender(QuarkusLokiLogAppenderRecorder recorder, QuarkusLokiLogAppenderConfig config) {
        System.out.println("Setting up logger");
        return new LogHandlerBuildItem(recorder.initializeLokiLogAppender(config));
    }

}
