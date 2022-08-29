package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
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
import static org.mockito.Mockito.*;

class DatastripManagementServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private DatastripService datastripService;
    @Mock
    private L0cPreparationProperties l0cPreparationProperties;

    @InjectMocks
    private DatastripManagementService datastripManagementService;

    private Datastrip readyDatastrip, notReadyDatastrip, deletableDatastrip, missingAuxDatastrip;
    private List<Datastrip> waitingDatastripList, readyDatastripList, notReadyDatastripList, deletableDatastripList, missingAuxDatastripList;

    @Override
    public void setup() throws Exception {

        missingAuxDatastrip = new Datastrip();
        Map<String, Boolean> availableByAux = new HashMap<>();
        availableByAux.put(AuxProductType.GIP_ATMIMA.name(), false);
        missingAuxDatastrip.setAvailableByAux(availableByAux);

        readyDatastrip = new Datastrip();
        readyDatastrip.setReady(true);

        notReadyDatastrip = new Datastrip();
        notReadyDatastrip.setAvailableByAux(Map.of(AuxProductType.GIP_LREXTR.name(), true));

        deletableDatastrip = new Datastrip();
        deletableDatastrip.setJobOrderCreated(true);
        deletableDatastrip.setLastModifiedDate(Instant.now().minus(1, ChronoUnit.HOURS));

        waitingDatastripList = List.of(missingAuxDatastrip);
        readyDatastripList = List.of(readyDatastrip);
        notReadyDatastripList = List.of(notReadyDatastrip);
        deletableDatastripList = List.of(deletableDatastrip);
        missingAuxDatastripList = List.of(missingAuxDatastrip);


        datastripManagementService = new DatastripManagementService(datastripService, catalogService, l0cPreparationProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getReady() {
        // Given
        when(datastripService.readAll(true, false, false)).thenReturn(readyDatastripList);
        // When
        final List<Datastrip> ready = datastripManagementService.getReady();
        // Then
        assertEquals(readyDatastripList, ready);
    }

    @Test
    void getDeletable() {
        // Given
        when(datastripService.readAllOr(true, true)).thenReturn(deletableDatastripList);
        // When
        final List<Datastrip> deletable = datastripManagementService.getDeletable();
        // Then
        assertEquals(deletableDatastripList, deletable);
    }

    @Test
    void getWaiting() {
        // Given
        when(datastripService.readAll(false, false)).thenReturn(waitingDatastripList);
        // When
        final List<Datastrip> waiting = datastripManagementService.getWaiting();
        // Then
        assertEquals(waitingDatastripList, waiting);
    }

    @Test
    void updateAvailableAux() {
        // Given
        when(datastripService.readAll(false, false, false)).thenReturn(missingAuxDatastripList);
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
        when(datastripService.readAll(false, false, false)).thenReturn(notReadyDatastripList);
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
        assertTrue(readyDatastripList.stream().allMatch(Datastrip::isJobOrderCreated));
    }

    @Test
    void create() {
        // Given
        when(datastripService.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(new Datastrip());
        // When
        datastripManagementService.create(Paths.get(TestHelper.FOLDER, TestHelper.DATASTRIP_NAME), TestHelper.SATELLITE, TestHelper.STATION_CODE, TestHelper.T0_PDGS_DATE);
        // Then
        verify(datastripService).create(any(), any(), any(), any(), any(), any(), any());
    }

    private void mockAuxCatalogResponse() {
        when(catalogService.retrieveLatestAuxData(any(), any(), any(), any())).thenReturn(Optional.of(new AuxCatalogData()));
    }

}
