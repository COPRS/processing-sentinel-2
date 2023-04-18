package eu.csgroup.coprs.ps2.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("cleanup")
public class CleanupProperties {

    /**
     * Enable local workspace cleanup
     */
    private boolean localEnabled = true;

    /**
     * Enable shared filesystem cleanup
     */
    private boolean sharedEnabled = true;

    /**
     * Set hours after which folder expired on shared filesystem
     */
    private int hours = 12;

}
