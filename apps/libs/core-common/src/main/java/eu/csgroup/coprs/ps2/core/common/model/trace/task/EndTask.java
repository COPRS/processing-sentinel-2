package eu.csgroup.coprs.ps2.core.common.model.trace.task;

import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.EmptyTaskOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.TaskOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.quality.EmptyTaskQuality;
import eu.csgroup.coprs.ps2.core.common.model.trace.quality.TaskQuality;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EndTask extends AbstractTask {

    private TaskStatus status;
    private Integer errorCode = 0;
    private Double durationInSeconds;

    private TaskOutput output;
    private TaskQuality quality;
    private TaskMissingOutput missingOutput;

    public EndTask() {
        this.setEvent(TaskEvent.END);
        this.setOutput(new EmptyTaskOutput());
        this.setQuality(new EmptyTaskQuality());
    }

}
