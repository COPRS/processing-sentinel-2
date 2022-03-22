package eu.csgroup.coprs.ps2.core.catalog.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SessionCatalogData extends BaseCatalogData {

    private String sessionId;
    private String startTime;
    private String stopTime;
    private String channelId;
    private Set<String> rawNames;

}
