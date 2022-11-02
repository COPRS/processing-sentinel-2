package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWCleanupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0uEWOutputServiceTest extends AbstractTest {

    @Mock
    private L0uEWUploadService uploadService;
    @Mock
    private L0uEWCopyService copyService;
    @Mock
    private L0uEWMessageService messageService;
    @Mock
    private L0uEWCleanupService cleanupService;
    @Mock
    private L0uExecutionProperties l0uExecutionProperties;

    @InjectMocks
    private L0uEWOutputService l0uEWOutputService;

    @Override
    public void setup() throws Exception {
        l0uEWOutputService = new L0uEWOutputService(messageService, cleanupService, uploadService, copyService, l0uExecutionProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void output() {
        // Given
        final Set<ProcessingMessage> messages = Set.of(new ProcessingMessage());
        when(messageService.build(any(), any(), any())).thenReturn(messages);
        when(l0uExecutionProperties.isCleanup()).thenReturn(true);
        // When
        final Set<ProcessingMessage> output = l0uEWOutputService.output(new L0uExecutionInput());
        // Then
        assertEquals(messages, output);
    }

}
