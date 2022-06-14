package eu.csgroup.coprs.ps2.core.catalog.settings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("catalog")
public class CatalogProperties {

    /**
     * URL for the Metadata Catalog
     */
    private String url;

    /**
     * Timeout for Metadata Catalog connections
     */
    private int timeout = 5;

    /**
     * Product Family for AUX queries
     */
    private String auxProductFamily = "S2_AUX";

    /**
     * Mode for AUX metadata request
     */
    private String mode = "LatestValCover";

    /**
     * Maximum retry count for Metadata Catalog queries
     */
    private int maxRetry = 3;

}
