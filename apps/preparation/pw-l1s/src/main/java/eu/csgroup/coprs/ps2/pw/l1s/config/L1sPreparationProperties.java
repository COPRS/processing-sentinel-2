package eu.csgroup.coprs.ps2.pw.l1s.config;

import eu.csgroup.coprs.ps2.core.pw.service.PWProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pw.l1s")
public class L1sPreparationProperties implements PWProperties {

    /**
     * Name of the OBS bucket containing AUX files
     */
    private String auxBucket;

    /**
     * Name of the OBS bucket containing L0c files
     */
    private String l0Bucket;

    /**
     * Path to the root of the filesystem shared between L1 containers
     */
    private String sharedFolderRoot;

    /**
     * Minimum number of granules required to start L1 processing chain
     */
    private long minGrRequired = 48;

}
