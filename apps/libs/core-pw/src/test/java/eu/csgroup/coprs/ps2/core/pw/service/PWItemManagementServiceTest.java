package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.pw.config.PWProperties;
import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import eu.csgroup.coprs.ps2.core.pw.model.helper.Item;
import eu.csgroup.coprs.ps2.core.pw.model.helper.ItemEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PWItemManagementServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private PWProperties pwProperties;
    @Mock
    private PWItemService<Item, ItemEntity> itemService;

    private PWItemManagementService<Item, ItemEntity, PWItemService<Item, ItemEntity>> itemManagementService;

    private List<Item> itemList;
    private Item item1;
    private Item item2;

    @Captor
    private ArgumentCaptor<Set<String>> setArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<Item>> itemArgumentCaptor;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {

        item1 = podamFactory.manufacturePojo(Item.class);
        item2 = podamFactory.manufacturePojo(Item.class);
        item1.setCreatedDate(Instant.now().minus(2, ChronoUnit.DAYS));
        item2.setCreatedDate(Instant.now().minus(2, ChronoUnit.HOURS));

        final HashMap<String, Boolean> availableByAux1 = new HashMap<>();
        availableByAux1.put(AuxProductType.GIP_ATMIMA.name(), false);
        availableByAux1.put(AuxProductType.GIP_VIEDIR.name(), false);
        availableByAux1.put(AuxProductType.GIP_CLOINV.name(), true);
        item1.setAvailableByAux(availableByAux1);
        item1.setLastModifiedDate(Instant.now().minus(2, ChronoUnit.MINUTES));

        final HashMap<String, Boolean> availableByAux2 = new HashMap<>();
        availableByAux2.put(AuxProductType.GIP_CLOINV.name(), true);
        item2.setAvailableByAux(availableByAux2);
        item2.setLastModifiedDate(Instant.now().minus(2, ChronoUnit.MINUTES));

        itemList = List.of(item1, item2);

        itemManagementService = mock(PWItemManagementService.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(itemManagementService, "itemService", itemService);
        ReflectionTestUtils.setField(itemManagementService, "catalogService", catalogService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void cleanup_test() {
        // Given
        when(itemService.readAllByJobOrderCreated(true)).thenReturn(itemList);
        doNothing().when(itemService).deleteAll(setArgumentCaptor.capture());
        // When
        itemManagementService.cleanup();
        // Then
        assertEquals(itemList.stream().map(PWItem::getName).collect(Collectors.toSet()), setArgumentCaptor.getValue());
    }

    @Test
    void cleanup_test_empty() {
        // Given
        when(itemManagementService.getDeletable()).thenReturn(Collections.emptyList());
        // When
        itemManagementService.cleanup();
        // Then
        verify(itemService, never()).deleteAll(any());
    }

    @Test
    void testUpdateAvailableAux() {
        // Given
        final Optional<AuxCatalogData> value = Optional.of(new AuxCatalogData());
        when(catalogService.retrieveLatestAuxData(any(), any(), any(), any())).thenReturn(value);
        when(catalogService.retrieveLatestAuxData(any(), any(), any(), any(), any())).thenReturn(value);
        // When
        itemManagementService.updateAvailableAux(item1);
        // Then
        verify(catalogService).retrieveLatestAuxData(any(), any(), any(), any());
        verify(catalogService, times(13)).retrieveLatestAuxData(any(), any(), any(), any(), any());
        assertTrue(item1.allAuxAvailable());
    }

    @Test
    void testUpdateAvailableAux_ready() {
        // When
        itemManagementService.updateAvailableAux(item2);
        // Then
        verify(catalogService, never()).retrieveLatestAuxData(any(), any(), any(), any());
        verify(catalogService, never()).retrieveLatestAuxData(any(), any(), any(), any(), any());
        assertTrue(item2.allAuxAvailable());
    }

    // TODO check whether to add tests for updateAvailableAux (all) and updateNotReady - or in each child's test class

}
