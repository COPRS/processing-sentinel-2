package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.exception.ProcessingException;
import eu.csgroup.coprs.ps2.core.ew.model.helper.Input;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EWProcessorServiceTest extends AbstractTest {

    private static final ProcessingMessage inputMessage = ProcessingMessageUtils.create();

    @Mock
    private EWInputService<Input> inputService;
    @Mock
    private EWSetupService<Input> setupService;
    @Mock
    private EWExecutionService<Input> executionService;
    @Mock
    private EWOutputService<Input> outputService;

    private EWProcessorService<Input> processorService;
    private Set<ProcessingMessage> outputMessageSet;
    private Input input;


    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {

        processorService = Mockito.mock(EWProcessorService.class, Mockito.CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(processorService, "inputService", inputService);
        ReflectionTestUtils.setField(processorService, "setupService", setupService);
        ReflectionTestUtils.setField(processorService, "executionService", executionService);
        ReflectionTestUtils.setField(processorService, "outputService", outputService);

        outputMessageSet = Set.of(ProcessingMessageUtils.create());

        input = podamFactory.manufacturePojo(Input.class);
        when(inputService.extract(inputMessage)).thenReturn(input);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void processMessage_nominal() {
        // Given
        when(outputService.output(input)).thenReturn(outputMessageSet);

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final Set<ProcessingMessage> output = processorService.processMessage(inputMessage);

            // Then
            verify(inputService).extract(inputMessage);
            verify(setupService).setup(input);
            verify(executionService).execute(eq(input), any());
            verify(outputService).output(input);

            assertEquals(outputMessageSet, output);

            assertEquals(2, logCaptor.getLogs().size());
            assertTrue(logCaptor.getLogs().stream().noneMatch(s -> s.contains("ERROR")));
        }
    }

    @Test
    void processMessage_error() {
        // Given
        when(outputService.output(input)).thenReturn(outputMessageSet);
        when(outputService.output(input)).thenThrow(new ProcessingException("Nope"));

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            assertThrows(ProcessingException.class, () -> processorService.processMessage(inputMessage));

            // Then
            verify(inputService).extract(inputMessage);
            verify(setupService).setup(input);
            verify(executionService).execute(eq(input), any());
            verify(outputService).output(input);

            assertEquals(2, logCaptor.getLogs().size());
            assertEquals(1, logCaptor.getLogs().stream().filter(s -> s.contains("ERROR")).count());
        }
    }

}
