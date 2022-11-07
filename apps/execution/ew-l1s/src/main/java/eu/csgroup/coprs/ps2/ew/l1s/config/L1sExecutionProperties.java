package eu.csgroup.coprs.ps2.ew.l1s.config;

import eu.csgroup.coprs.ps2.core.ew.config.L1ExecutionProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ew")
public class L1sExecutionProperties extends L1ExecutionProperties {

}
