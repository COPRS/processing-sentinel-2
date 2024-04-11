/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
