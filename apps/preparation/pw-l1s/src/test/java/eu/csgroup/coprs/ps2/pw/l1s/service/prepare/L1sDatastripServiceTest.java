package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripMapper;
import eu.csgroup.coprs.ps2.pw.l1s.repository.L1sDatastripEntityRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

class L1sDatastripServiceTest extends AbstractTest {

    @Mock
    private L1sDatastripEntityRepository repository;

    private final L1sDatastripMapper datastripMapper = Mappers.getMapper(L1sDatastripMapper.class);

    @InjectMocks
    private L1sDatastripService datastripService;

    @Override
    public void setup() throws Exception {
        datastripService = new L1sDatastripService(repository, datastripMapper);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void create() {
        // Given
        mockExists(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        // When
        final L1sDatastrip create = datastripService.create(
                TestHelper.DATASTRIP_NAME,
                TestHelper.FOLDER,
                TestHelper.DATASTRIP_TIMES,
                TestHelper.SATELLITE,
                TestHelper.T0_PDGS_DATE,
                TestHelper.DATATAKE_TYPE,
                TestHelper.GR_LIST);
        // Then
        assertNotNull(create);
        assertEquals(TestHelper.DATASTRIP_NAME, create.getName());
        assertEquals(TestHelper.FOLDER, create.getFolder());
        assertEquals(TestHelper.START_TIME, create.getStartTime());
        assertEquals(TestHelper.STOP_TIME, create.getStopTime());
        assertEquals(TestHelper.SATELLITE, create.getSatellite());
        assertEquals(TestHelper.T0_PDGS_DATE, create.getT0PdgsDate());
        assertEquals(TestHelper.DATATAKE_TYPE, create.getDatatakeType());
        assertNotNull(create.getAvailableByAux());
        assertTrue(create.getAvailableByAux().values().stream().noneMatch(Boolean::booleanValue));
        assertEquals(TestHelper.GR_LIST.size(), create.getAvailableByGR().size());
        assertTrue(create.getAvailableByGR().values().stream().noneMatch(Boolean::booleanValue));
    }

    @Test
    void create_exists() {
        // Given
        mockExists(true);
        // When Then
        assertThrows(MongoDBException.class, () -> datastripService.create(
                TestHelper.DATASTRIP_NAME,
                TestHelper.FOLDER,
                TestHelper.DATASTRIP_TIMES,
                TestHelper.SATELLITE,
                TestHelper.T0_PDGS_DATE,
                TestHelper.DATATAKE_TYPE,
                TestHelper.GR_LIST)
        );
    }

    @Test
    void readAll() {
        // Gven
        when(repository.findAllByGrCompleteAndReadyAndJobOrderCreated(anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(List.of(TestHelper.DATASTRIP_ENTITY));
        // When
        final List<L1sDatastrip> l1sDatastrips = datastripService.readAll(true, true, true);
        // Then
        assertEquals(1, l1sDatastrips.size());
    }

    @Test
    void update() {
        // Given
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mockFind();
        // When
        final L1sDatastrip update = datastripService.update(TestHelper.UPDATED_DATASTRIP);
        // Then
        assertNotNull(update);
        assertEquals(TestHelper.DATASTRIP_NAME, update.getName());
        assertEquals(TestHelper.FOLDER, update.getFolder());
        assertEquals(TestHelper.START_TIME, update.getStartTime());
        assertEquals(TestHelper.STOP_TIME, update.getStopTime());
        assertEquals(TestHelper.SATELLITE, update.getSatellite());
        assertEquals(TestHelper.T0_PDGS_DATE, update.getT0PdgsDate());
        assertEquals(TestHelper.DATATAKE_TYPE, update.getDatatakeType());
        assertTrue(update.isReady());
    }

    private void mockExists(boolean exists) {
        when(repository.existsById(TestHelper.DATASTRIP_NAME)).thenReturn(exists);
    }

    private void mockFind() {
        when(repository.findById(TestHelper.DATASTRIP_NAME)).thenReturn(Optional.of(TestHelper.DATASTRIP_ENTITY));
    }

}
