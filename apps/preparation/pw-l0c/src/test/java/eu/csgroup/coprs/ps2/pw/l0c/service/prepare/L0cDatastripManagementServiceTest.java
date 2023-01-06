package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class L0cDatastripManagementServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private L0cDatastripService datastripService;

    @InjectMocks
    private L0cDatastripManagementService datastripManagementService;

    private L0cDatastrip readyDatastrip, notReadyDatastrip, deletableDatastrip, missingAuxDatastrip;
    private List<L0cDatastrip> waitingDatastripList, readyDatastripList, notReadyDatastripList, deletableDatastripList, missingAuxDatastripList;

    @Override
    public void setup() throws Exception {

        missingAuxDatastrip = new L0cDatastrip();
        Map<String, Boolean> availableByAux = new HashMap<>();
        availableByAux.put(AuxProductType.GIP_ATMIMA.name(), false);
        missingAuxDatastrip.setAvailableByAux(availableByAux);

        readyDatastrip = new L0cDatastrip();
        readyDatastrip.setReady(true);

        notReadyDatastrip = new L0cDatastrip();
        notReadyDatastrip.setAvailableByAux(Map.of(AuxProductType.GIP_LREXTR.name(), true));

        deletableDatastrip = new L0cDatastrip();
        deletableDatastrip.setJobOrderCreated(true);
        deletableDatastrip.setLastModifiedDate(Instant.now().minus(1, ChronoUnit.HOURS));

        waitingDatastripList = List.of(missingAuxDatastrip);
        readyDatastripList = List.of(readyDatastrip);
        notReadyDatastripList = List.of(notReadyDatastrip);
        deletableDatastripList = List.of(deletableDatastrip);
        missingAuxDatastripList = List.of(missingAuxDatastrip);


        datastripManagementService = new L0cDatastripManagementService(datastripService, catalogService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getReady() {
        // Given
        when(datastripService.readAll(true, false)).thenReturn(readyDatastripList);
        // When
        final List<L0cDatastrip> ready = datastripManagementService.getReady();
        // Then
        assertEquals(readyDatastripList, ready);
    }

    @Test
    void getDeletable() {
        // Given
        when(datastripService.readAllByJobOrderCreated(true)).thenReturn(deletableDatastripList);
        // When
        final List<L0cDatastrip> deletable = datastripManagementService.getDeletable();
        // Then
        assertEquals(deletableDatastripList, deletable);
    }

    @Test
    void updateAvailableAux() {
        // Given
        when(datastripService.readAll(false, false)).thenReturn(missingAuxDatastripList);
        mockAuxCatalogResponse();
        // When
        datastripManagementService.updateAvailableAux();
        // Then
        verify(datastripService).updateAll(anyList());
        assertTrue(missingAuxDatastrip.allAuxAvailable());
    }

    @Test
    void updateNotReady() {
        // Given
        when(datastripService.readAll(false, false)).thenReturn(notReadyDatastripList);
        // When
        datastripManagementService.updateNotReady();
        // Then
        verify(datastripService).updateAll(anyList());
        assertTrue(notReadyDatastrip.isReady());
    }

    @Test
    void setJobOrderCreated() {
        // When
        datastripManagementService.setJobOrderCreated(readyDatastripList);
        // Then
        assertTrue(readyDatastripList.stream().allMatch(L0cDatastrip::isJobOrderCreated));
    }

    @Test
    void create() {
        // Given
        when(datastripService.create(any(), any(), any(), any(), any(), any())).thenReturn(new L0cDatastrip());
        // When
        datastripManagementService.create(Paths.get(TestHelper.DS_FOLDER, TestHelper.DATASTRIP_NAME), TestHelper.SATELLITE, TestHelper.STATION_CODE, TestHelper.T0_PDGS_DATE);
        // Then
        verify(datastripService).create(any(), any(), any(), any(), any(), any());
    }

    private void mockAuxCatalogResponse() {
        when(catalogService.retrieveLatestAuxData(any(), any(), any(), any())).thenReturn(Optional.of(new AuxCatalogData()));
    }

}
