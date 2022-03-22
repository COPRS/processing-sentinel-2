package eu.csgroup.coprs.ps2.ew.l0u.settings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ew.l0u")
public class L0uExecutionProperties {

    /**
     * Name of the OBS bucket to upload L0U SAD files to
     */
    private String sadUploadBucket;

    /**
     * Name of the OBS bucket to upload L0U HKTM files to
     */
    private String hktmUploadBucket;

    /**
     * Folder to store produced DS/GR. Mount-point to shared disk.
     */
    private String outputFolderRoot;

}
