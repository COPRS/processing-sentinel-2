package eu.csgroup.coprs.ps2.core.common.model.trace.task;

import eu.csgroup.coprs.ps2.core.common.model.trace.input.TaskInput;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class AbstractTask {

    private UUID uid;
    private String name;
    private TaskEvent event;
    private String satellite;

    private TaskInput input;

    @Override
    public String toString() {
        return "Task{" +
                "uid='" + uid + '\'' +
                ", event=" + event +
                '}';
    }

}
