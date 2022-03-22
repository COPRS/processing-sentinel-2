package eu.csgroup.coprs.ps2.core.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class StartupListener {

    List<String> filterList = List.of("credentials", "username", "password", "privateKey", "passphrase", "accessKey", "secretKey", "access-key", "secret-key");

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        final Environment env = contextRefreshedEvent.getApplicationContext().getEnvironment();

        log.debug("====== Environment and configuration ======");
        log.debug("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));

        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();

        StreamSupport.stream(sources.spliterator(), false)
                .filter(EnumerablePropertySource.class::isInstance)
                .map(propertySource -> ((EnumerablePropertySource) propertySource).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(property -> filterList.stream().noneMatch(property::contains))
                .forEach(property -> log.debug("{}: {}", property, env.getProperty(property)));

        log.debug("====== Application is running ======");
    }

}
