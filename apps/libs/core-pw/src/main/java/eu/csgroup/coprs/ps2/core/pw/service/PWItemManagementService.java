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

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.processing.Band;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import eu.csgroup.coprs.ps2.core.pw.model.PWItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class PWItemManagementService<S extends PWItem, I extends PWItemEntity, V extends PWItemService<S, I>> {

    private static final long DELETION_GRACE_PERIOD = 60L;

    protected final CatalogService catalogService;
    protected final V itemService;

    protected PWItemManagementService(CatalogService catalogService, V itemService) {
        this.catalogService = catalogService;
        this.itemService = itemService;
    }

    public abstract List<S> getMissingAux();

    public abstract boolean isReady(S item);

    public List<S> getReady() {
        return itemService.readAll(true, false);
    }

    public List<S> getNotReady() {
        return itemService.readAll(false, false);
    }

    public List<S> getDeletable() {
        final Instant now = Instant.now();
        return itemService.readAllByJobOrderCreated(true).stream()
                .filter(item -> Duration.between(item.getLastModifiedDate(), now).getSeconds() > DELETION_GRACE_PERIOD)
                .toList();
    }

    public void updateAvailableAux() {

        log.info("Updating AUX availability for all waiting items");

        final List<S> missingAux = getMissingAux();

        log.debug("Found {} items waiting for AUX", missingAux.size());

        if (!CollectionUtils.isEmpty(missingAux)) {
            missingAux.forEach(this::updateAvailableAux);
            itemService.updateAll(missingAux);
        }

        log.info("Finished updating AUX availability for all waiting items");
    }

    public void updateNotReady() {

        log.info("Updating ready status for all items not yet ready");

        final List<S> items = getNotReady();

        log.info("Found {} items not ready", items.size());

        if (!CollectionUtils.isEmpty(items)) {
            items.forEach(item -> {
                item.setReady(isReady(item));
                if (item.isReady()) {
                    log.info("Item {} is now ready", item.getName());
                }
            });
            itemService.updateAll(items);
        }
        log.info("Finished updating ready status for all items not yet ready");
    }

    public void setJobOrderCreated(List<S> itemList) {
        itemList.forEach(item -> item.setJobOrderCreated(true));
        itemService.updateAll(itemList);
    }

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

                        if (Boolean.FALSE.equals(entry.getValue())) {

                            if (auxProductType.isBandDependent()) {
                                entry.setValue(
                                        Band.allBandIndexIds().stream()
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
                            } else {
                                entry.setValue(
                                        catalogService.retrieveLatestAuxData(
                                                        auxProductType,
                                                        item.getSatellite(),
                                                        item.getStartTime(),
                                                        item.getStopTime())
                                                .isPresent());
                            }
                        }
                    });
        }

        log.debug("Finished updating AUX availability for item {}", item.getName());
    }

}
