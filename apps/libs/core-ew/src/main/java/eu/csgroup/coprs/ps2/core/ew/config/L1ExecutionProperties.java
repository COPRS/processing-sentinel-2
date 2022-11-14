package eu.csgroup.coprs.ps2.core.ew.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class L1ExecutionProperties {

    /**
     * Name of the OBS bucket containing AUX files
     */
    private String auxBucket;

    /**
     * Path to the root of the filesystem shared between L1 containers
     */
    private String sharedFolderRoot;

    /**
     * Path to the root of the filesystem containing DEM files
     */
    private String demFolderRoot;

    /**
     * Path to the root of the filesystem containing GRID files
     */
    private String gridFolderRoot;

    /**
     * Name of the OBS bucket containing L1 DS files
     */
    private String l1DSBucket;

    /**
     * Name of the OBS bucket containing L1 GR files
     */
    private String l1GRBucket;

    /**
     * Name of the OBS bucket containing L1 TL files
     */
    private String l1TLBucket;

    /**
     * Clean workspace after execution.
     */
    private boolean cleanup = true;

    /**
     * Maximum number of parallel processing tasks
     */
    private int maxParallelTasks = 8;

}
