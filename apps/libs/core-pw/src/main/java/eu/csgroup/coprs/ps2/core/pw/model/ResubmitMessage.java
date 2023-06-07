package eu.csgroup.coprs.ps2.core.pw.model;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResubmitMessage {

    private ProductFamily productFamily;
    private String keyObjectStorage;
    private String missionId;
    private String satelliteId;
    private String storagePath;
    private Instant t0PdgsDate;
}
