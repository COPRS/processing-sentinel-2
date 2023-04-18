package eu.csgroup.coprs.ps2.core.common.model.trace.missing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MissingOutputProductType {

    L0_GR("MSI_L0__GR"),
    L0_DS("MSI_L0__DS"),
    L1A_GR("MSI_L1A_GR"),
    L1A_DS("MSI_L1A_DS"),
    L1B_GR("MSI_L1B_GR"),
    L1B_DS("MSI_L1B_DS"),
    L1C_DS("MSI_L1C_DS"),
    L1C_TL("MSI_L1C_TL"),
    L2A_DS("MSI_L2A_DS"),
    L2A_TL("MSI_L2A_TL"),
    HKTM("PRD_HKTM"),
    SAD("AUX_SADATA");

    private final String type;

}
