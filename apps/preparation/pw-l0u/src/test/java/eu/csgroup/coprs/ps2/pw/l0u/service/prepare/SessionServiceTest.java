package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.mongo.exception.MongoDBException;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.model.SessionMapper;
import eu.csgroup.coprs.ps2.pw.l0u.repository.SessionEntityRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionServiceTest extends AbstractTest {

    @Mock
    private SessionEntityRepository sessionEntityRepository;

    private SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

    @InjectMocks
    private SessionService sessionService;

    @Override
    public void setup() throws Exception {
        sessionService = new SessionService(sessionEntityRepository, sessionMapper);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void create() {
        // Given
        mockExists(false);
        when(sessionEntityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        // When
        final Session create = sessionService.create(
                TestHelper.SESSION_NAME,
                TestHelper.START_TIME,
                TestHelper.STOP_TIME,
                TestHelper.SATELLITE,
                TestHelper.STATION_CODE,
                TestHelper.T0_PDGS_DATE);
        // Then
        assertNotNull(create);
        assertEquals(TestHelper.SESSION_NAME, create.getName());
        assertEquals(TestHelper.START_TIME, create.getStartTime());
        assertEquals(TestHelper.STOP_TIME, create.getStopTime());
        assertEquals(TestHelper.SATELLITE, create.getSatellite());
        assertEquals(TestHelper.STATION_CODE, create.getStationCode());
        assertEquals(TestHelper.T0_PDGS_DATE, create.getT0PdgsDate());
        assertNotNull(create.getAvailableByAux());
        assertTrue(create.getAvailableByAux().values().stream().noneMatch(Boolean::booleanValue));
        assertEquals(TestHelper.SESSION_NAME, create.getName());
    }

    @Test
    void create_exists() {
        // Given
        mockExists(true);
        // When Then
        assertThrows(MongoDBException.class, () -> sessionService.create(
                TestHelper.SESSION_NAME,
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
        final boolean exists = sessionService.exists(TestHelper.SESSION_NAME);
        // Then
        assertTrue(exists);
    }

    @Test
    void read() {
        // Given
        mockFind();
        // When
        final Session read = sessionService.read(TestHelper.SESSION_NAME);
        // Then
        assertEquals(TestHelper.SESSION_NAME, read.getName());
    }

    @Test
    void readAll_all() {
        // Given
        when(sessionEntityRepository.findAllByRawCompleteAndReadyAndJobOrderCreated(anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
                List.of(TestHelper.SESSION_ENTITY));
        // When
        final List<Session> sessions = sessionService.readAll(true, true, false);
        // Then
        assertEquals(1, sessions.size());
        assertEquals(TestHelper.SESSION_NAME, sessions.get(0).getName());
    }

    @Test
    void readAll_mid() {
        // Given
        when(sessionEntityRepository.findAllByReadyAndJobOrderCreated(anyBoolean(), anyBoolean())).thenReturn(List.of(TestHelper.SESSION_ENTITY));
        // When
        final List<Session> sessions = sessionService.readAll(true, true);
        // Then
        assertEquals(1, sessions.size());
        assertEquals(TestHelper.SESSION_NAME, sessions.get(0).getName());
    }

    @Test
    void readAll_short() {
        // Given
        when(sessionEntityRepository.findAllByJobOrderCreated(anyBoolean())).thenReturn(List.of(TestHelper.SESSION_ENTITY));
        // When
        final List<Session> sessions = sessionService.readAll(false);
        // Then
        assertEquals(1, sessions.size());
        assertEquals(TestHelper.SESSION_NAME, sessions.get(0).getName());
    }

    @Test
    void readAllOr() {
        // Given
        when(sessionEntityRepository.findAllByJobOrderCreated(anyBoolean())).thenReturn(List.of(TestHelper.SESSION_ENTITY));
        // When
        final List<Session> sessions = sessionService.readAll(false);
        // Then
        assertEquals(1, sessions.size());
        assertEquals(TestHelper.SESSION_NAME, sessions.get(0).getName());
    }

    @Test
    void update() {
        // Given
        when(sessionEntityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mockFind();
        // When
        final Session update = sessionService.update(TestHelper.UPDATED_SESSION);
        // Then
        assertNotNull(update);
        assertEquals(TestHelper.SESSION_NAME, update.getName());
        assertEquals(TestHelper.START_TIME, update.getStartTime());
        assertEquals(TestHelper.STOP_TIME, update.getStopTime());
        assertEquals(TestHelper.SATELLITE, update.getSatellite());
        assertEquals(TestHelper.STATION_CODE, update.getStationCode());
        assertEquals(TestHelper.T0_PDGS_DATE, update.getT0PdgsDate());
        assertTrue(update.isReady());
        assertTrue(update.isRawComplete());
    }

    @Test
    void updateAll() {
        // Given
        mockFind();
        when(sessionEntityRepository.saveAll(any())).thenReturn(null);
        // When
        sessionService.updateAll(List.of(TestHelper.SESSION));
        // Then
        verify(sessionEntityRepository).saveAll(any());
    }

    @Test
    void delete() {
        //Given
        mockFind();
        // When
        sessionService.delete(TestHelper.SESSION_NAME);
        // Then
        verify(sessionEntityRepository).delete(any());
    }

    @Test
    void deleteAll() {
        // Given
        // When
        final Set<String> nameList = Set.of(TestHelper.SESSION_NAME);
        sessionService.deleteAll(nameList);
        // Then
        verify(sessionEntityRepository).deleteAllByNameIn(nameList);
    }

    private void mockExists(boolean exists) {
        when(sessionEntityRepository.existsById(TestHelper.SESSION_NAME)).thenReturn(exists);
    }

    private void mockFind() {
        when(sessionEntityRepository.findById(TestHelper.SESSION_NAME)).thenReturn(Optional.of(TestHelper.SESSION_ENTITY));
    }

}
