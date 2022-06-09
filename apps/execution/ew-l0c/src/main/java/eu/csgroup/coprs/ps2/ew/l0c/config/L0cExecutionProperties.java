package eu.csgroup.coprs.ps2.ew.l0c.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ew.l0c")
public class L0cExecutionProperties {

    /**
     * Folder where L0U input files (DS/GR in DT folders) are stored. Mount-point to shared disk.
     */
    private String inputFolderRoot = "/input";

    /**
     * Folder where DEM files are stored. Mount-point to shared disk.
     */
    private String demFolderRoot = "/dem";

    /**
     * Name of the OBS bucket to upload DS L0C products to
     */
    private String dsUploadBucket;

    /**
     * Name of the OBS bucket to upload GR L0C products to
     */
    private String grUploadBucket;

    /**
     * Clean workspace after execution.
     */
    private boolean cleanup = true;

}
