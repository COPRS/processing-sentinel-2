package eu.csgroup.coprs.ps2.core.pw.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public abstract class PWPendingProcessingService {

    private final PWItemService<?, ?> itemService;

    protected PWPendingProcessingService(MeterRegistry registry, PWItemService<?, ?> itemService) {

        this.itemService = itemService;

        log.info("Setting up metric: Pending processing Gauge - Mission: S2 - Level: {} - Addon: {}", getLevel(), getAddonName());

        Gauge.builder("rs.pending.processing.job", fetchPendingSessions())
                .tag("mission", "S2")
                .tag("level", getLevel())
                .tag("addonName", getAddonName())
                .description("Show the number of pending processing entries")
                .register(registry);
    }

    protected abstract String getLevel();

    protected abstract String getAddonName();

    private Supplier<Number> fetchPendingSessions() {
        return () -> itemService.readAll(false, false).size();
    }

}
