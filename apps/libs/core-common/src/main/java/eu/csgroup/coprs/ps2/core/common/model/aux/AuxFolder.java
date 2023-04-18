package eu.csgroup.coprs.ps2.core.common.model.aux;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuxFolder {

    S2IPF_CAMS("S2IPF-CAMS"),
    S2IPF_ECMWF("S2IPF-ECMWF"),
    S2IPF_GIPP("S2IPF-GIPP"),
    S2IPF_IERS("S2IPF-IERS");

    private final String path;

}
