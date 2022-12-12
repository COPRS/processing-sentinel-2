package eu.csgroup.coprs.ps2.core.common.model.l2;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class L2ExecutionInput extends ExecutionInput {

    private String datastrip;
    private String auxFolder;
    private String inputFolder;
    private String outputFolder;
    private List<String> tileList;

    private String l2aDsPath;
    private String tile;

}
