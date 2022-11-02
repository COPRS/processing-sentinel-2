package eu.csgroup.coprs.ps2.pw.l1c.config;

import eu.csgroup.coprs.ps2.core.pw.service.PWProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pw.l1c")
public class L1cPreparationProperties implements PWProperties {

    /**
     * Name of the OBS bucket containing AUX files
     */
    private String auxBucket;

    /**
     * Path to the root of the filesystem shared between L1 containers
     */
    private String sharedFolderRoot;

}
