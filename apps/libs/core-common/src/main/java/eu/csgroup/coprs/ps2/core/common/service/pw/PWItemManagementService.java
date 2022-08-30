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

    protected PWItemManagementService(CatalogService catalogService, V itemService) {
        this.catalogService = catalogService;
        this.itemService = itemService;
    }


    public abstract void updateAvailableAux();

    public abstract void updateNotReady();

    public abstract List<S> getReady();
    public abstract List<S> getDeletable();

    public abstract void setJobOrderCreated(List<S> itemList);


    public void cleanup() {

        log.info("Removing created items");

        final List<S> deletableItems = getDeletable();

        log.debug("Found {} deletable items", deletableItems.size());

        if (!CollectionUtils.isEmpty(deletableItems)) {
            itemService.deleteAll(deletableItems.stream().map(S::getName).collect(Collectors.toSet()));
        }

        log.info("Finished Removing created items");
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
