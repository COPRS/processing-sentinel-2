package eu.csgroup.coprs.ps2.core.ew.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class L1ExecutionProperties {

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
     * Maximum number of parallel processing tasks
     */
    private int maxParallelTasks = 8;

}
