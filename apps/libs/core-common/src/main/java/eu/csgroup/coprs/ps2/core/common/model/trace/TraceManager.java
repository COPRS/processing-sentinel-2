package eu.csgroup.coprs.ps2.core.common.model.trace;


import eu.csgroup.coprs.ps2.core.common.model.trace.input.TaskInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.TaskOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.BeginTask;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.EndTask;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.TaskQuality;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Getter
@Setter
public class TraceManager {

    private final UUID uid = UUID.randomUUID();

    private String taskName;
    private String satellite;
    private UUID parentUid;
    private UUID predecessorUid;
    private Instant start = Instant.EPOCH;
    private TaskInput input = TaskInput.EMPTY;


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
                .setInput(input);

        final Trace trace = new Trace()
                .setHeader(new Header().setLevel(TraceLevel.INFO))
                .setMessage(new Message().setContent(message))
                .setTask(beginTask);

        TraceLogger.log(trace);
    }

    public void end(String message, TaskOutput output) {
        end(message, output, null);
    }

    public void end(String message, TaskOutput output, TaskQuality quality) {
        end(TraceLevel.INFO, TaskStatus.OK, message, output, quality);
    }

    public void warning(String message, TaskOutput output, TaskQuality quality) {
        end(TraceLevel.WARNING, TaskStatus.OK, message, output, quality);
    }

    public void error(String message) {
        end(TraceLevel.ERROR, TaskStatus.NOK, message, null, null);
    }

    public void end(TraceLevel level, TaskStatus status, String message, TaskOutput output, TaskQuality quality) {

        final Double duration = Duration.between(start, Instant.now()).toMillis() / 1000.0;

        final EndTask endTask = new EndTask();

        if (output != null) {
            endTask.setOutput(output);
        }
        if (quality != null) {
            endTask.setQuality(quality);
        }

        endTask.setStatus(status)
                .setDurationInSeconds(duration)
                .setUid(uid)
                .setName(taskName)
                .setSatellite(satellite)
                .setInput(input);

        final Trace trace = new Trace()
                .setHeader(new Header().setLevel(level))
                .setMessage(new Message().setContent(message))
                .setTask(endTask);

        TraceLogger.log(trace);
    }

}
