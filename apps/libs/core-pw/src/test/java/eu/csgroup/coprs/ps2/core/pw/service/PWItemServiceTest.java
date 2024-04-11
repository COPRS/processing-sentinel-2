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

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.core.pw.model.PWItemMapper;
import eu.csgroup.coprs.ps2.core.pw.model.helper.Item;
import eu.csgroup.coprs.ps2.core.pw.model.helper.ItemEntity;
import eu.csgroup.coprs.ps2.core.pw.repository.PWItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

class PWItemServiceTest extends AbstractTest {

    private static final String ITEM_NAME = "item";

    @Mock
    private PWItemRepository<ItemEntity> itemRepository;
    @Mock
    private PWItemMapper<Item, ItemEntity> itemMapper;

    private PWItemService<Item, ItemEntity> itemService;

    private ItemEntity itemEntity;
    private Item item;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {

        itemService = mock(PWItemService.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);

        itemEntity = ((ItemEntity) new ItemEntity().setName(ITEM_NAME));
        item = new Item(ITEM_NAME);
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void exists() {
        // Given
        mockExists(true);
        // When
        final boolean exists = itemService.exists(ITEM_NAME);
        // Then
        assertTrue(exists);
    }

    @Test
    void read() {
        // Given
        mockFind();
        mockMapper_Item();
        // When
        final Item read = itemService.read(ITEM_NAME);
        // Then
        assertEquals(ITEM_NAME, read.getName());
    }

    @Test
    void readAllByJobOrderCreated() {
        // Given
        when(itemRepository.findAllByJobOrderCreated(anyBoolean())).thenReturn(List.of(itemEntity));
        mockMapper_Item();
        // When
        final List<Item> sessions = itemService.readAllByJobOrderCreated(false);
        // Then
        assertEquals(1, sessions.size());
        assertEquals(ITEM_NAME, sessions.get(0).getName());
    }

    @Test
    void readAll() {
        // Given
        when(itemRepository.findAllByReadyAndJobOrderCreated(anyBoolean(), anyBoolean())).thenReturn(List.of(itemEntity));
        mockMapper_Item();
        // When
        final List<Item> sessions = itemService.readAll(true, true);
        // Then
        assertEquals(1, sessions.size());
        assertEquals(ITEM_NAME, sessions.get(0).getName());
    }

    @Test
    void delete() {
        //Given
        mockFind();
        // When
        itemService.delete(ITEM_NAME);
        // Then
        verify(itemRepository).delete(any());
    }

    @Test
    void deleteAll() {
        // Given
        // When
        final Set<String> nameList = Set.of(ITEM_NAME);
        itemService.deleteAll(nameList);
        // Then
        verify(itemRepository).deleteAllByNameIn(nameList);
    }

    @Test
    void update() {
        // Given
        when(itemRepository.save(any())).
                thenAnswer(invocation -> invocation.getArgument(0));
        doAnswer(invocation -> ((ItemEntity) invocation.getArgument(0)).setReady(true))
                .when(itemService).updateEntity(any(), any());
        mockFind();
        mockMapper_Item();
        mockMapper_ItemEntity();
        // When
        final Item update = itemService.update(((Item) new Item(ITEM_NAME).setReady(true)));
        // Then
        assertNotNull(update);
    }

    @Test
    void updateAll() {
        // Given
        mockFind();
        when(itemRepository.saveAll(any())).thenReturn(null);
        mockMapper_ItemEntity();
        // When
        itemService.updateAll(List.of(item));
        // Then
        verify(itemRepository).saveAll(any());
    }

    @Test
    void readEntity() {
        // Given
        mockFind();
        // When
        final ItemEntity itemEntity1 = itemService.readEntity(ITEM_NAME);
        // Then
        assertEquals(ITEM_NAME, itemEntity1.getName());
    }

    @Test
    void readEntity_fails() {
        // When Then
        assertThrows(MongoDBException.class, () -> itemService.readEntity(ITEM_NAME));
    }

    @Test
    void toItems() {
        // Given
        mockMapper_Item();
        // When
        final List<Item> items = itemService.toItems(List.of(itemEntity));
        // Then
        assertEquals(1, items.size());
        assertEquals(ITEM_NAME, items.get(0).getName());
    }

    private void mockExists(boolean exists) {
        when(itemRepository.existsById(ITEM_NAME)).thenReturn(exists);
    }

    private void mockFind() {
        when(itemRepository.findById(ITEM_NAME)).thenReturn(Optional.of(itemEntity));
    }

    private void mockMapper_Item() {
        when(itemMapper.toItem(any())).thenReturn(item);
    }

    private void mockMapper_ItemEntity() {
        when(itemMapper.toItemEntity(any())).thenReturn(itemEntity);
    }

}
