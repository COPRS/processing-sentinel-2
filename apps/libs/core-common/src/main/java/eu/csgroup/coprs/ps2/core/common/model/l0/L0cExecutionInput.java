package eu.csgroup.coprs.ps2.core.common.model.l0;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@Accessors(chain = true)
public class L0cExecutionInput extends ExecutionInput {

    private String datastrip;
    private String dtFolder;

    private Map<String, Map<String, String>> jobOrders;

    @Override
    public List<String> listJobOrders() {
        return jobOrders
                .values()
                .stream()
                .flatMap(jobOrderByName -> jobOrderByName.keySet().stream())
                .toList();
    }

}
