package eu.csgroup.coprs.ps2.core.common.model.l0;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;


@Getter
@Setter
@Accessors(chain = true)
public class L0cExecutionInput extends ExecutionInput {

    private String datastrip;
    private String dtFolder;

    private Map<String, Map<String, String>> jobOrders;

}
