package eu.csgroup.coprs.ps2.core.common.model.processing;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Timeliness {

    EMPTY(""),
    FAST24("FAST"),
    OPERATOR_DEMAND("OPERATOR-DEMAND"),
    NRT("NRT"),
    NTC("NTC"),
    PT("PT"),
    STC("STC");

    private String value;

    Timeliness(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
