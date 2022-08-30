package eu.csgroup.coprs.ps2.core.common.model.trace.missing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MissingOutputProductType {

    GR("MSI_L0__GR"),
    DS("MSI_L0__DS"),
    HKTM("PRD_HKTM"),
    SAD("AUX_SADATA");

    private final String type;

}
