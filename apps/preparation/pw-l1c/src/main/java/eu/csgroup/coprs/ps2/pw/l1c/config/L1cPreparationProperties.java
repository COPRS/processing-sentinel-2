package eu.csgroup.coprs.ps2.pw.l1c.config;

import eu.csgroup.coprs.ps2.core.common.service.pw.PWProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pw.l1c")
public class L1cPreparationProperties implements PWProperties {


}
