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

import java.util.function.BooleanSupplier;

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


    @BuildStep(onlyIf = IsFabric8KubernetesInfoRetrieverAvailable.class)
    ReflectiveClassBuildItem fabric8KubernetesInfoRetriever() {
        // since we only need reflection to the constructor of the class, we can specify `false` for both the methods and the fields arguments.
        return new ReflectiveClassBuildItem(false, false, "net.codinux.log.kubernetes.Fabric8KubernetesInfoRetriever");
    }

    @BuildStep(onlyIf = IsCodinuxKubernetesInfoRetrieverAvailable.class)
    ReflectiveClassBuildItem codinuxKubernetesInfoRetriever() {
        return new ReflectiveClassBuildItem(false, false, "net.codinux.log.kubernetes.CodinuxKubernetesInfoRetriever");
    }

    @BuildStep(onlyIf = IsCodinuxKubernetesInfoRetrieverAvailable.class)
    ReflectiveClassBuildItem codinuxKubernetesInfoRetrieverModel() {
        return new ReflectiveClassBuildItem(true, true,
                "net.codinux.log.kubernetes.model.Container", "net.codinux.log.kubernetes.model.Container$Companion",
                "net.codinux.log.kubernetes.model.ContainerState", "net.codinux.log.kubernetes.model.ContainerState$Companion",
                "net.codinux.log.kubernetes.model.ContainerStateRunning", "net.codinux.log.kubernetes.model.ContainerStateRunning$Companion",
                "net.codinux.log.kubernetes.model.ContainerStateTerminated", "net.codinux.log.kubernetes.model.ContainerStateTerminated$Companion",
                "net.codinux.log.kubernetes.model.ContainerStateWaiting", "net.codinux.log.kubernetes.model.ContainerState.ContainerStateWaiting",
                "net.codinux.log.kubernetes.model.ContainerStatus", "net.codinux.log.kubernetes.model.ContainerStatus$Companion",
                "net.codinux.log.kubernetes.model.ObjectMeta", "net.codinux.log.kubernetes.model.ObjectMeta$Companion",
                "net.codinux.log.kubernetes.model.Pod", "net.codinux.log.kubernetes.model.Pod$Companion",
                "net.codinux.log.kubernetes.model.PodSpec", "net.codinux.log.kubernetes.model.PodSpec$Companion",
                "net.codinux.log.kubernetes.model.PodStatus", "net.codinux.log.kubernetes.model.PodStatus$Companion"
        );
    }

    static class IsFabric8KubernetesInfoRetrieverAvailable extends IsClassAvailableSupplier {

        public IsFabric8KubernetesInfoRetrieverAvailable() {
            super("net.codinux.log.kubernetes.Fabric8KubernetesInfoRetriever");
        }
    }

    static class IsCodinuxKubernetesInfoRetrieverAvailable extends IsClassAvailableSupplier {

        public IsCodinuxKubernetesInfoRetrieverAvailable() {
            super("net.codinux.log.kubernetes.CodinuxKubernetesInfoRetriever");
        }
    }

    static class IsClassAvailableSupplier implements BooleanSupplier {

        private String className;

        public IsClassAvailableSupplier(String className) {
            this.className = className;
        }

        @Override
        public boolean getAsBoolean() {
            try {
                Class.forName(className);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
    }

}
