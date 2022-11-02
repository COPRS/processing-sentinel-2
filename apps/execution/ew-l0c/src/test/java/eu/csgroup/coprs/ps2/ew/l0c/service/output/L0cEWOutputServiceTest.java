package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l0c.config.L0cExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWCleanupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0cEWOutputServiceTest extends AbstractTest {

    @Mock
    private L0cEWUploadService uploadService;
    @Mock
    private L0cEWMessageService messageService;
    @Mock
    private L0cEWCleanupService cleanupService;
    @Mock
    private L0cExecutionProperties l0cExecutionProperties;

    @InjectMocks
    private L0cEWOutputService l0cEWOutputService;

    @Override
    public void setup() throws Exception {
        l0cEWOutputService = new L0cEWOutputService(messageService, cleanupService, uploadService, l0cExecutionProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void output() {
        // Given
        // Given
        final Set<ProcessingMessage> messages = Set.of(new ProcessingMessage());
        when(messageService.build(any(), any(), any())).thenReturn(messages);
        when(l0cExecutionProperties.isCleanup()).thenReturn(true);
        // When
        final Set<ProcessingMessage> output = l0cEWOutputService.output(new L0cExecutionInput());
        // Then
        assertEquals(messages, output);
    }

}
