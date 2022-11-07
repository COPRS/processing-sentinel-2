package eu.csgroup.coprs.ps2.pw.l0c.config;

import eu.csgroup.coprs.ps2.core.pw.config.PWProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pw.l0c")
public class L0cPreparationProperties implements PWProperties {

    /**
     * Folder where L0U input files (DS/GR in DT folders) are stored. Mount-point to shared disk.
     */
    private String inputFolderRoot = "/input";

    /**
     * Folder where DEM files are stored. Mount-point to shared disk.
     */
    private String demFolderRoot = "/dem";

    /**
     * Folder where DEM_GLOBEF files are stored.
     */
    private String globeFolderName = "S2IPF-DEMGLOBE";

    /**
     * Bucket where AUX files are stored
     */
    private String auxBucket;

}
