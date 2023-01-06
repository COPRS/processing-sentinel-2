package eu.csgroup.coprs.ps2.core.common.model.l0;

import eu.csgroup.coprs.ps2.core.common.model.L012ExecutionInput;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class L0cExecutionInput extends L012ExecutionInput {

    private String dtFolder;

}
