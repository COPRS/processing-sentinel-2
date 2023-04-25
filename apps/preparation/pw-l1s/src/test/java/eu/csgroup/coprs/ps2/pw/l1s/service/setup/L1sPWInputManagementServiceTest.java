package eu.csgroup.coprs.ps2.pw.l1s.service.setup;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sDatastripManagementService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L1sPWInputManagementServiceTest extends AbstractTest {

    @Mock
    private L1sDatastripManagementService managementService;

    @InjectMocks
    private L1sPWInputManagementService inputManagementService;

    private ProcessingMessage processingMessage;

    @Override
    public void setup() throws Exception {
        inputManagementService = new L1sPWInputManagementService(managementService);
        processingMessage = ProcessingMessageUtils.create();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void manageInput_ds() {
        // Given
        processingMessage.setKeyObjectStorage("DS");
        processingMessage.setStoragePath("s3://bucket/path/DS");
        processingMessage.setProductFamily(ProductFamily.S2_L0_DS);

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {
            final UUID uuid = inputManagementService.manageInput(processingMessage);

            // Then
            verify(managementService).create(any(), any(), any(), any());
            assertEquals(2, logCaptor.getLogs().size());
            assertNotNull(uuid);
        }
    }

    @Test
    void manageInput_gr() {
        // Given
        processingMessage.setKeyObjectStorage("GR");
        processingMessage.setStoragePath("s3://bucket/path/GR");
        processingMessage.setProductFamily(ProductFamily.S2_L0_GR);
        processingMessage.getMetadata().put(MessageParameters.DATASTRIP_ID_FIELD, "DS");

        // When
        final UUID uuid = inputManagementService.manageInput(processingMessage);

        // Then
        verify(managementService, never()).create(any(), any(), any(), any());
        verify(managementService).updateGRComplete(any());
        assertNotNull(uuid);
    }

    @Test
    void manageInput_aux() {
        // Given
        processingMessage.setKeyObjectStorage("AUX");
        processingMessage.setStoragePath("s3://bucket/path/AUX");
        processingMessage.setProductFamily(ProductFamily.S2_AUX);

        // When
        final UUID uuid = inputManagementService.manageInput(processingMessage);

        // Then
        verify(managementService, never()).create(any(), any(), any(), any());
        verify(managementService, never()).updateGRComplete(any());
        assertNotNull(uuid);
    }

    @Test
    void manageInput_error() {
        // Given
        processingMessage.setKeyObjectStorage("file.xml");
        processingMessage.setStoragePath("s3://bucket/path/file.xml");
        processingMessage.setProductFamily(ProductFamily.S2_L0_DS);
        doThrow(FileOperationException.class).when(managementService).create(any(), any(), any(), any());

        // When Then
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {
            assertThrows(FileOperationException.class, () -> inputManagementService.manageInput(processingMessage));
            final List<String> logs = logCaptor.getLogs();
            assertEquals(2, logs.size());
            assertEquals(1, logs.stream().filter(s -> s.contains("ERROR")).count());
        }
    }

}
