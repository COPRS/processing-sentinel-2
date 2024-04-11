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

import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import eu.csgroup.coprs.ps2.core.pw.model.PWItemEntity;
import eu.csgroup.coprs.ps2.core.pw.model.PWItemMapper;
import eu.csgroup.coprs.ps2.core.pw.repository.PWItemRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;


@Slf4j
public abstract class PWItemService<S extends PWItem, I extends PWItemEntity> {

    protected static final String RETRIEVING_MULTIPLE_ITEMS = "Retrieving multiple items ({})";

    protected final PWItemRepository<I> itemRepository;
    protected final PWItemMapper<S, I> itemMapper;

    protected PWItemService(PWItemRepository<I> itemRepository, PWItemMapper<S, I> itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    protected abstract void updateEntity(I itemEntity, I updatedItemEntity);


    public boolean exists(String itemName) {
        return itemRepository.existsById(itemName);
    }

    public S read(String itemName) {
        log.debug("Retrieving item: {}", itemName);
        return itemMapper.toItem(readEntity(itemName));
    }

    public List<S> readAllByJobOrderCreated(boolean jobOrderCreated) {
        final List<S> items = toItems(itemRepository.findAllByJobOrderCreated(jobOrderCreated));
        log.debug(RETRIEVING_MULTIPLE_ITEMS, items.size());
        return items;
    }

    public List<S> readAll(boolean ready, boolean jobOrderCreated) {
        final List<S> items = toItems(itemRepository.findAllByReadyAndJobOrderCreated(ready, jobOrderCreated));
        log.debug(RETRIEVING_MULTIPLE_ITEMS, items.size());
        return items;
    }

    public void delete(String itemName) {
        log.debug("Deleting item: {}", itemName);
        I itemEntity = readEntity(itemName);
        itemRepository.delete(itemEntity);
    }

    public void deleteAll(Set<String> itemNameSet) {
        log.debug("Deleting items ({})", itemNameSet.size());
        itemRepository.deleteAllByNameIn(itemNameSet);
    }

    public S update(S item) {

        log.debug("Updating item: {}", item.getName());

        I itemEntity = readEntity(item.getName());
        I updatedItemEntity = itemMapper.toItemEntity(item);

        updateEntity(itemEntity, updatedItemEntity);

        return itemMapper.toItem(itemRepository.save(itemEntity));
    }

    public void updateAll(List<S> itemList) {

        log.debug("Updating multiple items ({})", itemList.size());

        final List<I> updatedItemEntities = itemList.stream().map(itemMapper::toItemEntity).toList();

        final List<I> itemEntities = updatedItemEntities.stream()
                .map(updatedItemEntity -> {
                    final I sessionEntity = readEntity(updatedItemEntity.getName());
                    updateEntity(sessionEntity, updatedItemEntity);
                    return sessionEntity;
                })
                .toList();

        itemRepository.saveAll(itemEntities);
    }


    protected I readEntity(String itemName) {
        return itemRepository.findById(itemName)
                .orElseThrow(() -> new MongoDBException("Item not found: " + itemName));
    }

    protected List<S> toItems(List<I> itemEntityList) {
        return itemEntityList.stream()
                .map(itemMapper::toItem)
                .toList();
    }

}
