package eu.csgroup.coprs.ps2.core.common.model.trace.missing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductMetadata {

    private String productGroupId;
    private String productTypeString;
    private String platformSerialIdentifierString; // Satellite
    private String platformShortNameString = "SENTINEL-2";
    private int processingLevelInteger;
    private String processorVersionString;
    private Integer orbitNumberInteger;
    private Boolean productConsolidatedBoolean;

}
