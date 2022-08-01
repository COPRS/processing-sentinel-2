package eu.csgroup.coprs.ps2.core.common.model.trace;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class Header {

    private String type = "REPORT";
    private String mission = "S2";
    private String workflow = "NOMINAL";
    private String debugMode = "false";
    private String rsChainName; // TODO set that
    private String rsChanVersion;
    private List<String> tagList;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant timestamp = Instant.now();

    private TraceLevel level;

}
