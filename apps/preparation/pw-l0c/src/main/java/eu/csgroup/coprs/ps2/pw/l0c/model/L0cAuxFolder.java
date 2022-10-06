package eu.csgroup.coprs.ps2.pw.l0c.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cAuxFolder {

    GIPP("/workspace/steps_data/GIPP"),
    IERS("/workspace/steps_data/IERS");

    private final String path;

}
