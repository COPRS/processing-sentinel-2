package eu.csgroup.coprs.ps2.core.common.model.trace;

import eu.csgroup.coprs.ps2.core.common.config.ChainProperties;
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

    private String rsChainName = ChainProperties.getChainName();
    private String rsChainVersion = ChainProperties.getChainVersion();

    private List<String> tagList;

    private Instant timestamp = Instant.now();

    private TraceLevel level;

}
