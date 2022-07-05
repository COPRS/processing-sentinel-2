package eu.csgroup.coprs.ps2.core.common.model.trace;


import eu.csgroup.coprs.ps2.core.common.model.trace.task.AbstractTask;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trace {

    private Header header;
    private Message message;
    private AbstractTask task;

    @Override
    public String toString() {
        return "Trace{" +
                "task=" + task +
                '}';
    }

}
