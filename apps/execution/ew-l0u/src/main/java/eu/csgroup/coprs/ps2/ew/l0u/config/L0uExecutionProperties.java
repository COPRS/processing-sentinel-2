package eu.csgroup.coprs.ps2.ew.l0u.config;

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
     * Folder to store produced DS/GR. Mount-point to shared disk.
     */
    private String outputFolderRoot = "/output";

}
