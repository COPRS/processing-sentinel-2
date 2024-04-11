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

package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class SessionManagementServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionManagementService sessionManagementService;

    private Session waitingSession, readySession, notReadySession, deletableSession, missingAuxSession;
    private List<Session> waitingSessionList, readySessionList, notReadySessionList, deletableSessionList, missingAuxSessionList;

    @Override
    public void setup() {

        waitingSession = new Session().setRawComplete(false);
        waitingSession.setName(TestHelper.SESSION_NAME);

        missingAuxSession = new Session().setRawComplete(true);
        Map<String, Boolean> availableByAux = new HashMap<>();
        availableByAux.put(AuxProductType.GIP_ATMIMA.name(), false);
        missingAuxSession.setAvailableByAux(availableByAux);

        readySession = new Session();
        readySession.setReady(true);

        notReadySession = new Session().setRawComplete(true);
        notReadySession.setAvailableByAux(Map.of(AuxProductType.GIP_LREXTR.name(), true));

        deletableSession = new Session();
        deletableSession.setJobOrderCreated(true);
        deletableSession.setLastModifiedDate(Instant.now().minus(1, ChronoUnit.HOURS));

        waitingSessionList = List.of(waitingSession);
        readySessionList = List.of(readySession);
        notReadySessionList = List.of(notReadySession);
        deletableSessionList = List.of(deletableSession);
        missingAuxSessionList = List.of(missingAuxSession);

        sessionManagementService = new SessionManagementService(catalogService, sessionService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }


    @Test
    void getReady() {
        // Given
        when(sessionService.readAll(true, false)).thenReturn(readySessionList);
        // When
        final List<Session> ready = sessionManagementService.getReady();
        // Then
        assertEquals(readySessionList, ready);
    }

    @Test
    void getDeletable() {
        // Given
        when(sessionService.readAllByJobOrderCreated(true)).thenReturn(deletableSessionList);
        // When
        final List<Session> deletable = sessionManagementService.getDeletable();
        // Then
        assertEquals(deletableSessionList, deletable);
    }

    @Test
    void updateAvailableAux() {
        // Given
        when(sessionService.readAll(true, false, false)).thenReturn(missingAuxSessionList);
        mockAuxCatalogResponse();
        // When
        sessionManagementService.updateAvailableAux();
        // Then
        verify(sessionService).updateAll(anyList());
    }

    @Test
    void updateAvailableAux_empty() {
        // Given
        when(sessionService.readAll(true, false, false)).thenReturn(Collections.emptyList());
        // When
        sessionManagementService.updateAvailableAux();
        // Then
        verify(sessionService, never()).updateAll(anyList());
    }

    @Test
    void updateNotReady() {
        // Given
        when(sessionService.readAll(false, false)).thenReturn(notReadySessionList);
        // When
        sessionManagementService.updateNotReady();
        // Then
        verify(sessionService).updateAll(anyList());
        assertTrue(notReadySession.isReady());
    }

    @Test
    void updateNotReady_empty() {
        // Given
        when(sessionService.readAll(false, false)).thenReturn(Collections.emptyList());
        // When
        sessionManagementService.updateNotReady();
        // Then
        verify(sessionService, never()).updateAll(anyList());
    }

    @Test
    void setJobOrderCreated() {
        // When
        sessionManagementService.setJobOrderCreated(readySessionList);
        // Then
        assertTrue(readySessionList.stream().allMatch(Session::isJobOrderCreated));
    }

    @Test
    void create() {
        // Given
        when(sessionService.create(any(), any(), any(), any(), any(), any())).thenReturn(new Session());
        mockSessionCatalogResponse();
        // When
        sessionManagementService.create(TestHelper.SESSION_NAME, Instant.now().minus(1, ChronoUnit.HOURS));
        // Then
        verify(sessionService).create(any(), any(), any(), any(), any(), any());
    }

    @Test
    void updateRawComplete() {
        // Given
        when(sessionService.exists(TestHelper.SESSION_NAME)).thenReturn(true);
        when(sessionService.read(TestHelper.SESSION_NAME)).thenReturn(waitingSession);
        when(sessionService.update(ArgumentMatchers.any())).thenReturn(waitingSession);
        mockSessionCatalogResponse();
        // When
        sessionManagementService.updateRawComplete(TestHelper.SESSION_NAME);
        // Then
        assertTrue(waitingSession.isRawComplete());
        assertEquals(Instant.ofEpochSecond(1658322893), waitingSession.getT0PdgsDate());
    }

    private void mockAuxCatalogResponse() {
        when(catalogService.retrieveLatestAuxData(any(), any(), any(), any())).thenReturn(Optional.of(new AuxCatalogData()));
    }

    private void mockSessionCatalogResponse() {
        when(catalogService.retrieveSessionData(TestHelper.SESSION_NAME)).thenReturn(TestHelper.SESSION_CATALOG_DATA_LIST);
    }

}
