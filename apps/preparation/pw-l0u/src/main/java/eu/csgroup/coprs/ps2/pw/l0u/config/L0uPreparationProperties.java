package eu.csgroup.coprs.ps2.pw.l0u.config;

import eu.csgroup.coprs.ps2.core.pw.service.PWProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pw.l0u")
public class L0uPreparationProperties implements PWProperties {

    /**
     * Bucket where AUX files are stored
     */
    private String auxBucket;

    /**
     * Bucket where RAW and DSIB files are stored
     */
    private String caduBucket;

}
