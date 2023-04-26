package eu.csgroup.coprs.ps2.core.ew.config;

import eu.csgroup.coprs.ps2.core.common.utils.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-core-ew.yaml", factory = YamlPropertySourceFactory.class)
public class MissingOutputPropertiesConfiguration {
}
