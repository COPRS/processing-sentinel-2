package eu.csgroup.coprs.ps2.core.common.model.l01;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrchestratorMode {

    L0C("L0"),
    OLQC_L0DS("OLQC_L0DS"),
    OLQC_L0GR("OLQC_L0GR"),
    L1A("L1A"),
    L1B("L1B"),
    L1B_NO_GRI("L1BNoGRI"),
    L1A_FORMAT_GR("L1AFormatGR"),
    L1A_FORMAT_DS("L1AFormatDS"),
    OLQC_L1BDS("OLQC_L1BDS"),
    L1B_FORMAT_GR("L1BFormatGR"),
    OLQC_L1CDS("OLQC_L1CDS"),
    L1C_TILE("L1CTile"),
    OLQC_L1CTL("OLQC_L1CTL");

    private final String mode;

}
