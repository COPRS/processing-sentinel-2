package eu.csgroup.coprs.ps2.core.common.model.l0;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cAuxFolders {

    GIPP("/workspace/steps_data/GIPP"),
    IERS("/workspace/steps_data/IERS");

    private String path;
}
