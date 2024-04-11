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

package eu.csgroup.coprs.ps2.core.pw.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.function.Supplier;

@Slf4j
@Service
@ConditionalOnBean(PWItemService.class)
public class PWPendingProcessingService {

    @Value("${pending.gaugeName}")
    private String gaugeName;

    @Value("${pending.mission}")
    private String mission;

    @Value("${pending.level}")
    private String level;

    @Value("${pending.addon}")
    private String addon;

    private final MeterRegistry registry;
    private final PWItemService<?, ?> itemService;

    public PWPendingProcessingService(MeterRegistry registry, PWItemService<?, ?> itemService) {
        this.registry = registry;
        this.itemService = itemService;
    }

    @PostConstruct
    public void init() {

        log.info("Setting up metric: Pending processing Gauge - Mission: {} - Level: {} - Addon: {}", mission, level, addon);

        Gauge.builder(gaugeName, fetchPendingSessions())
                .tag("mission", mission)
                .tag("level", level)
                .tag("addonName", addon)
                .description("Show the number of pending processing entries")
                .register(registry);
    }

    private Supplier<Number> fetchPendingSessions() {
        return () -> itemService.readAll(false, false).size();
    }

}
