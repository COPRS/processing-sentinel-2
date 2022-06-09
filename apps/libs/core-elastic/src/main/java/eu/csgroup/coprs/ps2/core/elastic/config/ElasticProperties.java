package eu.csgroup.coprs.ps2.core.elastic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("elastic")
public class ElasticProperties {

    private String host;

    private String port;

}
