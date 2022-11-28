package eu.csgroup.coprs.ps2.core.obs.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("obs.bucket")
public class ObsBucketProperties {

    /**
     * Name of the OBS bucket containing AUX files
     */
    private String auxBucket;

    /**
     * Bucket where sessions files are stored
     */
    private String sessionBucket;

    /**
     * Name of the OBS bucket containing SAD files
     */
    private String sadBucket;

    /**
     * Name of the OBS bucket containing HKTM files
     */
    private String hktmBucket;

    /**
     * Name of the OBS bucket containing L0c DS files
     */
    private String l0DSBucket;

    /**
     * Name of the OBS bucket containing L0c GR files
     */
    private String l0GRBucket;

    /**
     * Name of the OBS bucket containing L1 DS files
     */
    private String l1DSBucket;

    /**
     * Name of the OBS bucket containing L1 GR files
     */
    private String l1GRBucket;

    /**
     * Name of the OBS bucket containing L1 TL files
     */
    private String l1TLBucket;

    /**
     * Name of the OBS bucket containing L2A DS files
     */
    private String l2DSBucket;

    /**
     * Name of the OBS bucket containing L2A TL files
     */
    private String l2TLBucket;

}
