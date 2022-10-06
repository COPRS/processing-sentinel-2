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

    private String auxBucket;
    private String sharedFolderRoot;
    private String demFolderRoot;

}
