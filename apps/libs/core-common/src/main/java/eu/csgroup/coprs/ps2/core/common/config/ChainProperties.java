package eu.csgroup.coprs.ps2.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("chain")
public class ChainProperties {

    private String name;
    private String version;

    // Below :
    // Quick and dirty Workaround for trace headers and missing outputs
    // Should be refactored later with a cleaner solution

    private static String CHAIN_NAME; // NOSONAR
    private static String CHAIN_VERSION; // NOSONAR

    @Value("${chain.name}")
    public void setNameStatic(String name) {
        ChainProperties.CHAIN_NAME = name; // NOSONAR
    }

    @Value("${chain.version}")
    public void setVersionStatic(String version) {
        ChainProperties.CHAIN_VERSION = version; // NOSONAR
    }

    public static String getChainName() {
        return CHAIN_NAME;
    }

    public static String getChainVersion() {
        return CHAIN_VERSION;
    }

}
