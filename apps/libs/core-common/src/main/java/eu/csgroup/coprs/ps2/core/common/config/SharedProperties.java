package eu.csgroup.coprs.ps2.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ps2")
public class SharedProperties {

    /**
     * Path to the root of the shared filesystem
     */
    private String sharedFolderRoot = "/shared";

    /**
     * Folder where DEM files are stored. Mount-point to shared disk.
     */
    private String demFolderRoot = "/dem";

    /**
     * Path to the root of the filesystem containing GRID files
     */
    private String gridFolderRoot = "/grid";

    /**
     * Minimum number of granules required to start L1 processing chain
     */
    private long minGrRequired = 48;

    /**
     * Maximum number of parallel processing tasks (for L1 & L2)
     */
    private int maxParallelTasks = 8;

    /**
     * Timeout in seconds for each orchestrator step to complete (for L1)
     */
    private int killTimeout = 7200;

}
