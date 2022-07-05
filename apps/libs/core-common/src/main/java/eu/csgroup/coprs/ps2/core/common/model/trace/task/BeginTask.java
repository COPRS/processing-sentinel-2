package eu.csgroup.coprs.ps2.core.common.model.trace.task;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BeginTask extends AbstractTask {

    private UUID childOfTask;
    private UUID followsFromTask;

    public BeginTask() {
        this.setEvent(TaskEvent.BEGIN);
    }

}
