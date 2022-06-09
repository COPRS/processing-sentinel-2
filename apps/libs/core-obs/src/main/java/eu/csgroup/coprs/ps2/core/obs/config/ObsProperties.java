package eu.csgroup.coprs.ps2.core.obs.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties("obs")
public class ObsProperties {

    /**
     * Define Obs endpoint
     */
    private String endpoint;

    /**
     * Define Obs region
     */
    private String region;

    /**
     * Define Obs access key
     */
    private String accessKey;

    /**
     * Define Obs secret key
     */
    private String secretKey;

    /**
     * Define Obs maximum simultaneous connections; used for parallel and multipart transfers
     */
    private Integer maxConcurrency;

    /**
     * Define Obs maximum throughput, in Gb
     */
    private Double maxThroughput;

    /**
     * Define Obs minimum part size; in MB; for multipart transfers
     */
    private Long minimumPartSize;

    /**
     * Define Obs maximum number of retries on error
     */
    private int maxRetries;

}
