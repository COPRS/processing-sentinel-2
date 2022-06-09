package eu.csgroup.coprs.ps2.core.common.model.l0;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;


@Getter
@Setter
@Accessors(chain = true)
public class L0uExecutionInput extends ExecutionInput {

    private String session;

    private Map<String, String> jobOrders;

}
