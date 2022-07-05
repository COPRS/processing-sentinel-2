package eu.csgroup.coprs.ps2.core.common.model;

import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public abstract class PWItem {

    protected String name;

    protected String satellite;
    protected String stationCode;

    protected Instant createdDate;
    protected Instant lastModifiedDate;

    protected Instant startTime;
    protected Instant stopTime;

    protected Instant t0PdgsDate;

    protected Map<String, Boolean> availableByAux;

    protected boolean ready;
    protected boolean failed;
    protected boolean jobOrderCreated;

    public boolean allAuxAvailable() {
        return availableByAux.values().stream().allMatch(Boolean::booleanValue);
    }

    public String getSatelliteName() {
        return Mission.S2.getValue() + satellite;
    }

}
