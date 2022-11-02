package eu.csgroup.coprs.ps2.core.ew.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrchestratorMode {

    L0C("L0C"),
    L1A("L1A"),
    L1B("L1B"),
    L1B_NO_GRI("L1BNoGRI"),
    L1A_FORMAT_GR("L1AFormatGR"),
    L1A_FORMAT_DS("L1AFormatDS"),
    L1A_GR("L1A_GR"),
    L1A_DS("L1A_DS"),
    OLQC_L1BDS("OLQC_L1BDS"),
    L1B_FORMAT_GR("L1B_FormatGR"),
    L1B_DS("L1B_DS"),
    L1B_GR("L1B_GR"),
    L1C_TILE("L1CTile"),
    OLQC_L1CTL("OLGC_L1CTL"),
    L1C_T("L1C_T");

    private final String mode;

}
