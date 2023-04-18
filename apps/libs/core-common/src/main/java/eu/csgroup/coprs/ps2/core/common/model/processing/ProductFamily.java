package eu.csgroup.coprs.ps2.core.common.model.processing;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum ProductFamily {
    EDRS_SESSION,
    S2_L0_GR,
    S2_L0_DS,
    S2_L1A_GR,
    S2_L1A_DS,
    S2_L1B_GR,
    S2_L1B_DS,
    S2_L1C_TL,
    S2_L1C_DS,
    S2_L1C_TC,
    S2_L2A_TL,
    S2_L2A_DS,
    S2_L2A_TC,
    S2_AUX,
    S2_SAD,
    S2_HKTM,
    @JsonEnumDefaultValue
    UNKNOWN

}
