package eu.csgroup.coprs.ps2.core.common.model.l1;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class L1ExecutionInput extends ExecutionInput {

    private String datastrip;
    private DatatakeType datatakeType;
    private String auxFolder;
    private String inputFolder;
    private String outputFolder;

    @Override
    public List<String> listJobOrders() {
        return Collections.emptyList();
    }

}
