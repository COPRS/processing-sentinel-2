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

    private String auxBucket;
    private String l0Bucket;
    private String sharedFolderRoot;
    private String demFolderRoot;
    private long minGrRequired = 48;

}
