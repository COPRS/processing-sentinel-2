package eu.csgroup.coprs.ps2.core.catalog.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BaseCatalogData {

    private String productName;
    private String productType;
    private String keyObjectStorage;
    private String validityStart;
    private String validityStop;
    private String missionId;
    private String satelliteId;
    private String stationCode;
    private String swathType;
    private Map<String, Object> additionalProperties;

}
