package eu.csgroup.coprs.ps2.core.common.model.trace;


import eu.csgroup.coprs.ps2.core.common.model.trace.input.EmptyTaskInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.input.TaskInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.TaskOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.quality.TaskQuality;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.BeginTask;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.EndTask;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
@Setter
public class TaskReport {

    private final UUID uid = UUID.randomUUID();

    private String taskName;
    private String satellite;
    private UUID parentUid;
    private UUID predecessorUid;
    private Instant start = Instant.EPOCH;
    private TaskInput input = new EmptyTaskInput();


    public void begin(String message) {
        begin(message, null);
    }

    public void begin(String message, TaskInput input) {

        this.start = Instant.now();

        if (input != null) {
            this.input = input;
        }

        final BeginTask beginTask = new BeginTask();
        beginTask.setChildOfTask(parentUid)
                .setFollowsFromTask(predecessorUid)
                .setUid(uid)
                .setName(taskName)
                .setSatellite(satellite)
                .setInput(this.input);

        final Trace trace = new Trace()
                .setHeader(new Header().setLevel(TraceLevel.INFO))
                .setMessage(new Message().setContent(message))
                .setTask(beginTask);

        TraceLogger.log(trace);
    }

    public void end(String message) {
        end(new EndWrapper()
                .setLevel(TraceLevel.INFO)
                .setStatus(TaskStatus.OK)
                .setMessage(message));
    }

    public void end(String message, TaskOutput output, List<TaskMissingOutput> missingOutputs) {
        end(new EndWrapper()
                .setLevel(TraceLevel.INFO)
                .setStatus(TaskStatus.OK)
                .setMessage(message)
                .setOutput(output)
                .setMissingOutputs(missingOutputs));
    }

    public void end(String message, Double dataRateMebibytesSec, Double dataVolumeMebibytes) {
        end(new EndWrapper()
                .setLevel(TraceLevel.INFO)
                .setStatus(TaskStatus.OK)
                .setMessage(message)
                .setDataRateMebibytesSec(dataRateMebibytesSec)
                .setDataVolumeMebibytes(dataVolumeMebibytes));
    }

    public void warning(String message) {
        end(new EndWrapper()
                .setLevel(TraceLevel.WARNING)
                .setStatus(TaskStatus.OK)
                .setMessage(message));
    }

    public void error(String message) {
        end(new EndWrapper()
                .setLevel(TraceLevel.ERROR)
                .setStatus(TaskStatus.NOK)
                .setMessage(message));
    }

    public void error(String message, List<TaskMissingOutput> missingOutputs) {
        end(new EndWrapper()
                .setLevel(TraceLevel.ERROR)
                .setStatus(TaskStatus.NOK)
                .setMessage(message)
                .setMissingOutputs(missingOutputs));
    }

    private void end(EndWrapper wrapper) {

        final Double duration = Duration.between(start, Instant.now()).toMillis() / 1000.0;

        final EndTask endTask = new EndTask();

        endTask.setDataRateMebibytesSec(wrapper.getDataRateMebibytesSec());
        endTask.setDataVolumeMebibytes(wrapper.getDataVolumeMebibytes());

        if (wrapper.getOutput() != null) {
            endTask.setOutput(wrapper.getOutput());
        }
        if (wrapper.getQuality() != null) {
            endTask.setQuality(wrapper.getQuality());
        }
        endTask.setMissingOutput(wrapper.getMissingOutputs());

        endTask.setStatus(wrapper.getStatus())
                .setDurationInSeconds(duration)
                .setUid(uid)
                .setName(taskName)
                .setSatellite(satellite)
                .setInput(input);

        final Trace trace = new Trace()
                .setHeader(new Header().setLevel(wrapper.getLevel()))
                .setMessage(new Message().setContent(wrapper.getMessage()))
                .setTask(endTask);

        TraceLogger.log(trace);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class EndWrapper {

        private TraceLevel level;
        private TaskStatus status;
        private String message;
        private TaskOutput output;
        private TaskQuality quality;
        private List<TaskMissingOutput> missingOutputs;
        private Double dataRateMebibytesSec;
        private Double dataVolumeMebibytes;

    }

}
