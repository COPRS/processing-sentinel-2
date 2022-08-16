package eu.csgroup.coprs.ps2.core.common.model.l0;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Getter
@Setter
@Accessors(chain = true)
public class L0uExecutionInput extends ExecutionInput {

    private String session;

    private Map<String, String> jobOrders;

    @Override
    public List<String> listJobOrders() {
        return jobOrders.keySet().stream().toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof L0uExecutionInput that)) return false;
        return Objects.equals(session, that.session) && Objects.equals(jobOrders, that.jobOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, jobOrders);
    }

}
