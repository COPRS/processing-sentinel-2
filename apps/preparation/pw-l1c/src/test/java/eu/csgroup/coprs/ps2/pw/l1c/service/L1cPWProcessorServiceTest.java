package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L1cPWProcessorServiceTest extends AbstractTest {

    @Mock
    private L1cPWInputManagementService inputManagementService;
    @Mock
    private L1cPWTileManagementService tileManagementService;
    @Mock
    private L1cPWMessageService messageService;

    @InjectMocks
    private L1cPWProcessorService processorService;

    @Override
    public void setup() throws Exception {
        processorService = new L1cPWProcessorService(inputManagementService, tileManagementService, messageService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void processMessage() {
        // Given
        final Set<String> tileSet = Set.of("tile1", "tile2");
        final Set<ProcessingMessage> messageSet = Set.of(ProcessingMessageUtils.create(), ProcessingMessageUtils.create());
        when(inputManagementService.manageInput(any())).thenReturn(new L1ExecutionInput());
        when(tileManagementService.listTiles(any())).thenReturn(tileSet);
        when(messageService.build(any(), any())).thenReturn(messageSet);
        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {
            final Set<ProcessingMessage> outputMessages = processorService.processMessage(ProcessingMessageUtils.create());
            final List<String> logs = logCaptor.getLogs();
            // Then
            assertEquals(4, logs.size());
            assertEquals(outputMessages, messageSet);
        }
    }


}
