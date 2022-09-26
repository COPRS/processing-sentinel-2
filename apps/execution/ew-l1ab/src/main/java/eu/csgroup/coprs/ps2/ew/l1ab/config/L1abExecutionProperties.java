package eu.csgroup.coprs.ps2.ew.l1ab.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ew.l1ab")
public class L1abExecutionProperties {

}
