package eu.csgroup.coprs.ps2.core.common.model.script;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class ScriptWrapper {

    @NotBlank
    private String runId;
    private String workdir;
    private List<String> command;
    private List<String> logWhitelist = Collections.emptyList();
    private Map<String, String> environment = Collections.emptyMap();
    private Long timeOut = null;
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public String[] getCommandArgs() {
        return command.toArray(new String[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScriptWrapper that)) return false;
        return runId.equals(that.runId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId);
    }

}
