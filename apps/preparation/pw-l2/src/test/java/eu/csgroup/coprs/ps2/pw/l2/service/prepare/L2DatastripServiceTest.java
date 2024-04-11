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

package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripMapper;
import eu.csgroup.coprs.ps2.pw.l2.repository.L2DatastripEntityRepository;
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

class L2DatastripServiceTest extends AbstractTest {

    @Mock
    private L2DatastripEntityRepository repository;

    private final L2DatastripMapper datastripMapper = Mappers.getMapper(L2DatastripMapper.class);

    @InjectMocks
    private L2DatastripService datastripService;

    @Override
    public void setup() throws Exception {
        datastripService = new L2DatastripService(repository, datastripMapper);
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
        final L2Datastrip create = datastripService.create(
                TestHelper.DATASTRIP_NAME,
                TestHelper.FOLDER,
                TestHelper.DATASTRIP_TIMES,
                TestHelper.SATELLITE,
                TestHelper.T0_PDGS_DATE,
                TestHelper.TL_LIST);
        // Then
        assertNotNull(create);
        assertEquals(TestHelper.DATASTRIP_NAME, create.getName());
        assertEquals(TestHelper.FOLDER, create.getFolder());
        assertEquals(TestHelper.START_TIME, create.getStartTime());
        assertEquals(TestHelper.STOP_TIME, create.getStopTime());
        assertEquals(TestHelper.SATELLITE, create.getSatellite());
        assertEquals(TestHelper.T0_PDGS_DATE, create.getT0PdgsDate());
        assertNotNull(create.getAvailableByAux());
        assertTrue(create.getAvailableByAux().values().stream().noneMatch(Boolean::booleanValue));
        assertEquals(TestHelper.TL_LIST.size(), create.getAvailableByTL().size());
        assertTrue(create.getAvailableByTL().values().stream().noneMatch(Boolean::booleanValue));
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
                TestHelper.TL_LIST)
        );
    }

    @Test
    void readAll() {
        // Gven
        when(repository.findAllByTlCompleteAndReadyAndJobOrderCreated(anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(List.of(TestHelper.DATASTRIP_ENTITY));
        // When
        final List<L2Datastrip> L2Datastrips = datastripService.readAll(true, true, true);
        // Then
        assertEquals(1, L2Datastrips.size());
    }

    @Test
    void update() {
        // Given
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mockFind();
        // When
        final L2Datastrip update = datastripService.update(TestHelper.UPDATED_DATASTRIP);
        // Then
        assertNotNull(update);
        assertEquals(TestHelper.DATASTRIP_NAME, update.getName());
        assertEquals(TestHelper.FOLDER, update.getFolder());
        assertEquals(TestHelper.START_TIME, update.getStartTime());
        assertEquals(TestHelper.STOP_TIME, update.getStopTime());
        assertEquals(TestHelper.SATELLITE, update.getSatellite());
        assertEquals(TestHelper.T0_PDGS_DATE, update.getT0PdgsDate());
        assertTrue(update.isReady());
    }

    private void mockExists(boolean exists) {
        when(repository.existsById(TestHelper.DATASTRIP_NAME)).thenReturn(exists);
    }

    private void mockFind() {
        when(repository.findById(TestHelper.DATASTRIP_NAME)).thenReturn(Optional.of(TestHelper.DATASTRIP_ENTITY));
    }

}
