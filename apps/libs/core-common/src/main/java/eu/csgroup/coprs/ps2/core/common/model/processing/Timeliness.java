package eu.csgroup.coprs.ps2.core.common.model.processing;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Timeliness {

    EMPTY(""),
    FAST24("FAST"),
    OPERATOR_DEMAND("OPERATOR-DEMAND"),
    NRT("NRT"),
    NTC("NTC"),
    PT("PT"),
    STC("STC"),
    S2_SESSION("S2_SESSION"),
    S2_L0("S2_L0"),
    S2_L1("S2_L1"),
    S2_L2("S2_L2");

    private String value;

    Timeliness(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
