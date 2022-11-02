package eu.csgroup.coprs.ps2.ew.l1s.config;

import eu.csgroup.coprs.ps2.core.ew.config.ExecutionProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ew.l1s")
public class L1sExecutionProperties extends ExecutionProperties {

    /**
     * Name of the OBS bucket containing L0c files
     */
    private String l0Bucket;

}
