package eu.csgroup.coprs.ps2.pw.l0c.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuxFolder {

    GIPP("/workspace/steps_data/GIPP"),
    IERS("/workspace/steps_data/IERS");

    private final String path;

}
