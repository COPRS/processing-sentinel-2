package eu.csgroup.coprs.ps2.ew.l1ab.config;

import eu.csgroup.coprs.ps2.core.ew.config.ExecutionProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ew.l1ab")
public class L1abExecutionProperties extends ExecutionProperties {

    /**
     * Name of the OBS bucket containing L1 files
     */
    private String l1Bucket;

}
