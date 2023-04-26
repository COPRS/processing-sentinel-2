package eu.csgroup.coprs.ps2.core.ew.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("missing")
public class MissingOutputProperties {

    /**
     * Version of the IPF script used for level 0u processing
     */
    private String l0uIpfVersion;

    /**
     * Version of the IPF script used for level 0c processing
     */
    private String l0cIpfVersion;

    /**
     * Version of the IPF script used for level 1 processing
     */
    private String l1IpfVersion;

    /**
     * Version of the IPF script used for level 2 processing
     */
    private String l2IpfVersion;

    /**
     * Ratio representing an (arbitrary) average number of granules per Tile.
     */
    private double grToTlRatio;

    /**
     * Default number of missing L0 Datastrip.
     */
    private int l0uDefaultDsCount;

    /**
     * Arbitrary number of missing HouseKeeping TeleMetry (HKTM) auxiliary files.
     */
    private int l0uDefaultHktmCount;

    /**
     * Arbitrary number of missing Satellite Ancillary Data (SAD) auxiliary files.
     */
    private int l0uDefaultSadCount;

    /**
     * Arbitrary number of missing Granules when L0c processing fails and it is unable to count the granules.
     */
    private int l0cDefaultGrCount;

}
