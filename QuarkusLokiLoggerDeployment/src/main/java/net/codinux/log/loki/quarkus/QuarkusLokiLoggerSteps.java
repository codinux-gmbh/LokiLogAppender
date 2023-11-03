package net.codinux.log.loki.quarkus;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import net.codinux.log.loki.model.Stream;
import net.codinux.log.loki.model.StreamBody;
import net.codinux.log.loki.model.Values;
import net.codinux.log.loki.quarkus.config.QuarkusLokiLogAppenderConfig;

public class QuarkusLokiLoggerSteps {

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public LogHandlerBuildItem setUpLogAppender(QuarkusLokiLogAppenderRecorder recorder, QuarkusLokiLogAppenderConfig config) {
        return new LogHandlerBuildItem(recorder.initializeLokiLogAppender(config));
    }

    @BuildStep
    ReflectiveClassBuildItem lokiLoggerClasses() {
        return new ReflectiveClassBuildItem(true, true,
                StreamBody.class, StreamBody.Companion.getClass(),
                Stream.class, Stream.Companion.getClass(),
                Values.class, Values.Companion.getClass(), Values.ValuesSerializer.class
        );
    }

}
