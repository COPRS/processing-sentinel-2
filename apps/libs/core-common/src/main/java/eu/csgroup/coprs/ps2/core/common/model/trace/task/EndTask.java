package eu.csgroup.coprs.ps2.core.common.model.trace.task;

import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.TaskOutput;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;


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
        this.setOutput(TaskOutput.EMPTY);
        this.setQuality(TaskQuality.EMPTY);
    }

}
