package eu.csgroup.coprs.ps2.core.common.model.l1;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;


@Getter
@Setter
@Accessors(chain = true)
public class L1cExecutionInput extends ExecutionInput {

    @Override
    public List<String> listJobOrders() {
        return Collections.emptyList();
    }

}
