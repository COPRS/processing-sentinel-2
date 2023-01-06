package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripMapper;
import eu.csgroup.coprs.ps2.pw.l0c.repository.L0cDatastripEntityRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0cDatastripServiceTest extends AbstractTest {

    @Mock
    private L0cDatastripEntityRepository datastripEntityRepository;

    private final L0cDatastripMapper datastripMapper = Mappers.getMapper(L0cDatastripMapper.class);

    @InjectMocks
    private L0cDatastripService datastripService;

    @Override
    public void setup() throws Exception {
        datastripService = new L0cDatastripService(datastripEntityRepository, datastripMapper);
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
        final L0cDatastrip create = datastripService.create(
                TestHelper.DATASTRIP_PATH,
                TestHelper.START_TIME,
                TestHelper.STOP_TIME,
                TestHelper.SATELLITE,
                TestHelper.STATION_CODE,
                TestHelper.T0_PDGS_DATE);
        // Then
        assertNotNull(create);
        assertEquals(TestHelper.DATASTRIP_NAME, create.getName());
        assertEquals(TestHelper.DS_FOLDER, create.getFolder());
        assertEquals(TestHelper.DT_FOLDER, create.getDtFolder());
        assertEquals(TestHelper.START_TIME, create.getStartTime());
        assertEquals(TestHelper.STOP_TIME, create.getStopTime());
        assertEquals(TestHelper.SATELLITE, create.getSatellite());
        assertEquals(TestHelper.STATION_CODE, create.getStationCode());
        assertEquals(TestHelper.T0_PDGS_DATE, create.getT0PdgsDate());
        assertNotNull(create.getAvailableByAux());
        assertTrue(create.getAvailableByAux().values().stream().noneMatch(Boolean::booleanValue));
    }

    @Test
    void create_exists() {
        // Given
        mockExists(true);
        // When Then
        assertThrows(MongoDBException.class, () -> datastripService.create(
                TestHelper.DATASTRIP_PATH,
                TestHelper.START_TIME,
                TestHelper.STOP_TIME,
                TestHelper.SATELLITE,
                TestHelper.STATION_CODE,
                TestHelper.T0_PDGS_DATE)
        );
    }

    @Test
    void update() {
        // Given
        when(datastripEntityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mockFind();
        // When
        final L0cDatastrip update = datastripService.update(TestHelper.UPDATED_DATASTRIP);
        // Then
        assertNotNull(update);
        assertEquals(TestHelper.DATASTRIP_NAME, update.getName());
        assertEquals(TestHelper.DS_FOLDER, update.getFolder());
        assertEquals(TestHelper.DT_FOLDER, update.getDtFolder());
        assertEquals(TestHelper.START_TIME, update.getStartTime());
        assertEquals(TestHelper.STOP_TIME, update.getStopTime());
        assertEquals(TestHelper.SATELLITE, update.getSatellite());
        assertEquals(TestHelper.STATION_CODE, update.getStationCode());
        assertEquals(TestHelper.T0_PDGS_DATE, update.getT0PdgsDate());
        assertTrue(update.isReady());
    }

    private void mockExists(boolean exists) {
        when(datastripEntityRepository.existsById(TestHelper.DATASTRIP_NAME)).thenReturn(exists);
    }

    private void mockFind() {
        when(datastripEntityRepository.findById(TestHelper.DATASTRIP_NAME)).thenReturn(Optional.of(TestHelper.DATASTRIP_ENTITY));
    }

}
