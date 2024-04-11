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

package eu.csgroup.coprs.ps2.pw.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionManagementService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L0uPWInputManagementServiceTest extends AbstractTest {

    private static final Instant now = Instant.now();

    @Mock
    private SessionManagementService managementService;

    @InjectMocks
    private L0uPWInputManagementService inputManagementService;

    private ProcessingMessage processingMessage;

    @Override
    public void setup() throws Exception {
        inputManagementService = new L0uPWInputManagementService(managementService);
        processingMessage = ProcessingMessageUtils.create();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void manageInput_dsib() {

        // Given
        processingMessage.setKeyObjectStorage("file.xml");
        processingMessage.setStoragePath("s3://bucket/path/file.xml");
        processingMessage.setProductFamily(ProductFamily.EDRS_SESSION);

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {
            final UUID uuid = inputManagementService.manageInput(processingMessage);

            // Then
            verify(managementService).create(any(), any());
            assertEquals(2, logCaptor.getLogs().size());
            assertNotNull(uuid);
        }
    }

    @Test
    void manageInput_raw() {

        // Given
        processingMessage.setKeyObjectStorage("file.raw");
        processingMessage.setStoragePath("s3://bucket/path/file.raw");
        processingMessage.setProductFamily(ProductFamily.EDRS_SESSION);

        // When
        final UUID uuid = inputManagementService.manageInput(processingMessage);

        // Then
        verify(managementService, never()).create(any(), any());
        verify(managementService).updateRawComplete(any());
        assertNotNull(uuid);
    }

    @Test
    void manageInput_aux() {

        // Given
        processingMessage.setKeyObjectStorage("file");
        processingMessage.setStoragePath("s3://bucket/path/file");
        processingMessage.setProductFamily(ProductFamily.S2_AUX);

        // When
        final UUID uuid = inputManagementService.manageInput(processingMessage);

        // Then
        verify(managementService, never()).create(any(), any());
        verify(managementService, never()).updateRawComplete(any());
        assertNotNull(uuid);
    }

    @Test
    void manageInput_t0_additional() {

        // Given
        processingMessage.setKeyObjectStorage("file.xml");
        processingMessage.setStoragePath("s3://bucket/path/file.xml");
        processingMessage.setProductFamily(ProductFamily.EDRS_SESSION);
        processingMessage.getAdditionalFields().put(MessageParameters.T0_PDGS_DATE_FIELD, now);

        // When
        inputManagementService.manageInput(processingMessage);

        // Then
        verify(managementService).create(any(), eq(now));
    }

    @Test
    void manageInput_t0_metadata() {

        // Given
        processingMessage.setKeyObjectStorage("file.xml");
        processingMessage.setStoragePath("s3://bucket/path/file.xml");
        processingMessage.setProductFamily(ProductFamily.EDRS_SESSION);
        processingMessage.getMetadata().put(MessageParameters.T0_PDGS_DATE_FIELD, now);

        // When
        inputManagementService.manageInput(processingMessage);

        // Then
        verify(managementService).create(any(), eq(now));
    }

    @Test
    void manageInput_error() {

        // Given
        processingMessage.setKeyObjectStorage("file.xml");
        processingMessage.setStoragePath("s3://bucket/path/file.xml");
        processingMessage.setProductFamily(ProductFamily.EDRS_SESSION);
        doThrow(FileOperationException.class).when(managementService).create(any(), any());

        // When Then
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {
            assertThrows(FileOperationException.class, () -> inputManagementService.manageInput(processingMessage));
            final List<String> logs = logCaptor.getLogs();
            assertEquals(2, logs.size());
            assertEquals(1, logs.stream().filter(s -> s.contains("ERROR")).count());
        }
    }

}
