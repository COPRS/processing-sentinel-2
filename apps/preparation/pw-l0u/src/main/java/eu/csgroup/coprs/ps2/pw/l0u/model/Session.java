package eu.csgroup.coprs.ps2.pw.l0u.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;


@Getter
@Setter
public class Session {

    private String name;

    private String satellite;
    private String stationCode;

    private Instant creationDate;

    private Instant startTime;
    private Instant stopTime;

    private boolean rawComplete;
    private Map<String, Boolean> availableByAux;

    private boolean ready;
    private boolean failed;
    private boolean jobOrderCreated;

    public boolean allAuxAvailable() {
        return availableByAux.values().stream().allMatch(Boolean::booleanValue);
    }

}
