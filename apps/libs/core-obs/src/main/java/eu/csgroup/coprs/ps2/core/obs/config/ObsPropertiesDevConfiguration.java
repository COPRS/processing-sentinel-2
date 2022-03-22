package eu.csgroup.coprs.ps2.core.obs.config;

import eu.csgroup.coprs.ps2.core.common.utils.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("dev")
@PropertySource(value = "classpath:application-core-obs-dev.yaml", factory = YamlPropertySourceFactory.class)
public class ObsPropertiesDevConfiguration {

}
