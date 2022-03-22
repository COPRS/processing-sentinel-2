package eu.csgroup.coprs.ps2.core.common.model.processing;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Mission {

    S1("S1"),
    S2("S2"),
    S3("S3"),
    UNKNOWN("");

    private String value;

    Mission(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
