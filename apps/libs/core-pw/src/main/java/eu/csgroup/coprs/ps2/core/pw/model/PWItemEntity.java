package eu.csgroup.coprs.ps2.core.pw.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Document(collection = "PW_Item")
public class PWItemEntity extends AuditableEntity {

    @Id
    private String name;

    private String satellite;
    private String stationCode;

    private Instant startTime;
    private Instant stopTime;

    private Instant t0PdgsDate;

    private Map<String, Boolean> availableByAux;

    private boolean ready;
    private boolean jobOrderCreated;

}
