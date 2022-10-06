package eu.csgroup.coprs.ps2.ew.l1s.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ew.l1s")
public class L1sExecutionProperties {

    private String auxBucket;
    private String l0Bucket;
    private String sharedFolderRoot;
    private String demFolderRoot;

}
