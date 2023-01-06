package eu.csgroup.coprs.ps2.core.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class L012ExecutionInput extends ExecutionInput {

    private String datastrip;
    private String auxFolder;
    private String inputFolder;
    private String outputFolder;
    private String tile;

}
