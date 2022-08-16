package eu.csgroup.coprs.ps2.core.common.service.pw;

import eu.csgroup.coprs.ps2.core.common.model.PWItem;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class PWItemManagementService<S extends PWItem, V extends PWItemService<S>> {

    protected final CatalogService catalogService;
    protected final V itemService;
    private final PWProperties pwProperties;

    protected PWItemManagementService(CatalogService catalogService, V itemService, PWProperties pwProperties) {
        this.catalogService = catalogService;
        this.itemService = itemService;
        this.pwProperties = pwProperties;
    }


    public abstract void updateAvailableAux();

    public abstract void updateNotReady();

    public abstract List<S> getReady();
    public abstract List<S> getDeletable();
    public abstract List<S> getWaiting();

    public abstract void setJobOrderCreated(List<S> itemList);


    public void cleanup() {

        log.info("Removing created or failed items");

        final List<S> deletableItems = getDeletable();

        log.debug("Found {} deletable items", deletableItems.size());

        if (!CollectionUtils.isEmpty(deletableItems)) {
            itemService.deleteAll(deletableItems.stream().map(S::getName).collect(Collectors.toSet()));
        }

        log.info("Finished Removing created or failed items");
    }

    public void updateFailed() {

        log.info("Updating failed status for all waiting items");

        final List<S> waitingItems = getWaiting();

        log.debug("Found {} waiting items", waitingItems.size());

        if (!CollectionUtils.isEmpty(waitingItems)) {
            waitingItems.forEach(item -> {
                final Instant creationDate = item.getCreatedDate();
                if (Duration.between(creationDate, Instant.now()).toHours() > pwProperties.getFailedDelay()) {
                    log.info("Failing item {}", item.getName());
                    item.setFailed(true);
                    // TODO send message to DLQ OR Trace ?
                }
            });
            itemService.updateAll(waitingItems);
        }

        log.info("Finished updating failed status for all waiting items");
    }

    protected void updateAvailableAux(S item) {

        log.debug("Updating AUX availability for item {}", item.getName());

        if (!item.allAuxAvailable()) {

            item.getAvailableByAux()
                    .entrySet()
                    .forEach(entry -> {

                        final AuxProductType auxProductType = AuxProductType.valueOf(entry.getKey());
                        final List<String> bandList = auxProductType.getBandList();

                        if (Boolean.FALSE.equals(entry.getValue())) {

                            if (CollectionUtils.isEmpty(bandList)) {
                                entry.setValue(
                                        catalogService.retrieveLatestAuxData(
                                                        auxProductType,
                                                        item.getSatellite(),
                                                        item.getStartTime(),
                                                        item.getStopTime())
                                                .isPresent());
                            } else {
                                entry.setValue(
                                        bandList.stream()
                                                .map(bandIndexId ->
                                                        catalogService.retrieveLatestAuxData(
                                                                        auxProductType,
                                                                        item.getSatellite(),
                                                                        item.getStartTime(),
                                                                        item.getStopTime(),
                                                                        bandIndexId)
                                                                .isPresent())
                                                .allMatch(isPresent -> true)
                                );
                            }
                        }
                    });
        }

        log.debug("Finished updating AUX availability for item {}", item.getName());
    }

}
