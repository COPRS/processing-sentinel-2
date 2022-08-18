package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.mongo.exception.MongoDBException;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.DatastripMapper;
import eu.csgroup.coprs.ps2.pw.l0c.repository.DatastripEntityRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

class DatastripServiceTest extends AbstractTest {

    @Mock
    private DatastripEntityRepository datastripEntityRepository;

    private final DatastripMapper datastripMapper = Mappers.getMapper(DatastripMapper.class);

    @InjectMocks
    private DatastripService datastripService;

    @Override
    public void setup() throws Exception {
        datastripService = new DatastripService(datastripEntityRepository, datastripMapper);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void create() {
        // Given
        mockExists(false);
        when(datastripEntityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        // When
        final Datastrip create = datastripService.create(
                TestHelper.DATASTRIP_NAME,
                TestHelper.FOLDER,
                TestHelper.START_TIME,
                TestHelper.STOP_TIME,
                TestHelper.SATELLITE,
                TestHelper.STATION_CODE,
                TestHelper.T0_PDGS_DATE);
        // Then
        assertNotNull(create);
        assertEquals(TestHelper.DATASTRIP_NAME, create.getName());
        assertEquals(TestHelper.FOLDER, create.getFolder());
        assertEquals(TestHelper.START_TIME, create.getStartTime());
        assertEquals(TestHelper.STOP_TIME, create.getStopTime());
        assertEquals(TestHelper.SATELLITE, create.getSatellite());
        assertEquals(TestHelper.STATION_CODE, create.getStationCode());
        assertEquals(TestHelper.T0_PDGS_DATE, create.getT0PdgsDate());
        assertNotNull(create.getAvailableByAux());
        assertTrue(create.getAvailableByAux().values().stream().noneMatch(Boolean::booleanValue));
        assertEquals(TestHelper.DATASTRIP_NAME, create.getName());
    }

    @Test
    void ecreate_xists() {
        // Given
        mockExists(true);
        // When Then
        assertThrows(MongoDBException.class, () -> datastripService.create(
                TestHelper.DATASTRIP_NAME,
                TestHelper.FOLDER,
                TestHelper.START_TIME,
                TestHelper.STOP_TIME,
                TestHelper.SATELLITE,
                TestHelper.STATION_CODE,
                TestHelper.T0_PDGS_DATE)
        );
    }

    @Test
    void exists() {
        // Given
        mockExists(true);
        // When
        final boolean exists = datastripService.exists(TestHelper.DATASTRIP_NAME);
        // Then
        assertTrue(exists);
    }

    @Test
    void read() {
        // Given
        mockFind();
        // When
        final Datastrip read = datastripService.read(TestHelper.DATASTRIP_NAME);
        // Then
        assertEquals(TestHelper.DATASTRIP_NAME, read.getName());
    }

    @Test
    void readAll_Full() {
        // Given
        when(datastripEntityRepository.findAllByReadyAndFailedAndJobOrderCreated(anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
                List.of(TestHelper.DATASTRIP_ENTITY));
        // When
        final List<Datastrip> datastrips = datastripService.readAll(true, false, true);
        // Then
        assertEquals(1, datastrips.size());
        assertEquals(TestHelper.DATASTRIP_NAME, datastrips.get(0).getName());
    }

    @Test
    void readAll() {
        // Given
        when(datastripEntityRepository.findAllByFailedAndJobOrderCreated(anyBoolean(), anyBoolean())).thenReturn(
                List.of(TestHelper.DATASTRIP_ENTITY));
        // When
        final List<Datastrip> datastrips = datastripService.readAll(true, false);
        // Then
        assertEquals(1, datastrips.size());
        assertEquals(TestHelper.DATASTRIP_NAME, datastrips.get(0).getName());
    }

    @Test
    void readAllOr() {
        // Given
        when(datastripEntityRepository.findAllByFailedOrJobOrderCreated(anyBoolean(), anyBoolean())).thenReturn(
                List.of(TestHelper.DATASTRIP_ENTITY));
        // When
        final List<Datastrip> datastrips = datastripService.readAllOr(true, false);
        // Then
        assertEquals(1, datastrips.size());
        assertEquals(TestHelper.DATASTRIP_NAME, datastrips.get(0).getName());
    }

    @Test
    void update() {
        // Given
        when(datastripEntityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mockFind();
        // When
        final Datastrip update = datastripService.update(TestHelper.UPDATED_DATASTRIP);
        // Then
        assertNotNull(update);
        assertEquals(TestHelper.DATASTRIP_NAME, update.getName());
        assertEquals(TestHelper.FOLDER, update.getFolder());
        assertEquals(TestHelper.START_TIME, update.getStartTime());
        assertEquals(TestHelper.STOP_TIME, update.getStopTime());
        assertEquals(TestHelper.SATELLITE, update.getSatellite());
        assertEquals(TestHelper.STATION_CODE, update.getStationCode());
        assertEquals(TestHelper.T0_PDGS_DATE, update.getT0PdgsDate());
        assertTrue(update.isReady());
    }

    @Test
    void updateAll() {
        // Given
        mockFind();
        when(datastripEntityRepository.saveAll(any())).thenReturn(null);
        // When
        datastripService.updateAll(List.of(TestHelper.DATASTRIP));
        // Then
        verify(datastripEntityRepository).saveAll(any());
    }

    @Test
    void delete() {
        //Given
        mockFind();
        // When
        datastripService.delete(TestHelper.DATASTRIP_NAME);
        // Then
        verify(datastripEntityRepository).delete(any());
    }

    @Test
    void deleteAll() {
        // Given
        // When
        final Set<String> nameList = Set.of(TestHelper.DATASTRIP_NAME);
        datastripService.deleteAll(nameList);
        // Then
        verify(datastripEntityRepository).deleteAllByNameIn(nameList);
    }

    private void mockExists(boolean exists) {
        when(datastripEntityRepository.existsById(TestHelper.DATASTRIP_NAME)).thenReturn(exists);
    }

    private void mockFind() {
        when(datastripEntityRepository.findById(TestHelper.DATASTRIP_NAME)).thenReturn(Optional.of(TestHelper.DATASTRIP_ENTITY));
    }

}
