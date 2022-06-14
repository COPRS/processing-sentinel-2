package eu.csgroup.coprs.ps2.pw.l0c.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;


@Getter
@Setter
@Document(collection = "Datastrip")
public class DatastripEntity {

    @Id
    private String name;

    private String dt;
    private String folder;

    private String satellite;
    private String stationCode;

    private Instant creationDate;

    private Instant startTime;
    private Instant stopTime;

    private Map<String, Boolean> availableByAux;

    private boolean ready;
    private boolean failed;
    private boolean jobOrderCreated;

    public boolean allAuxAvailable() {
        return availableByAux.values().stream().allMatch(Boolean::booleanValue);
    }

}
