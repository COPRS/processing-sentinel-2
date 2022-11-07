package eu.csgroup.coprs.ps2.pw.l1s.config;

import eu.csgroup.coprs.ps2.core.pw.config.L1PreparationProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pw")
public class L1sPreparationProperties extends L1PreparationProperties {

    /**
     * Name of the OBS bucket containing L0c DS files
     */
    private String l0DSBucket;

    /**
     * Name of the OBS bucket containing L0c DS files
     */
    private String l0GRBucket;

    /**
     * Minimum number of granules required to start L1 processing chain
     */
    private long minGrRequired = 48;

}
