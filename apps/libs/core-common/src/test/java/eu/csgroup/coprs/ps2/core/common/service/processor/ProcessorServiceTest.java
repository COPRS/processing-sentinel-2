package eu.csgroup.coprs.ps2.core.common.service.processor;

import eu.csgroup.coprs.ps2.core.common.exception.ProcessingException;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProcessorServiceTest extends AbstractTest {

    private static final ProcessingMessage inputMessage = ProcessingMessageUtils.create();
    private static final ProcessingMessage outputMessage = ProcessingMessageUtils.create();

    private ProcessorService processorService;

    @Override
    public void setup() throws Exception {
        processorService = Mockito.mock(ProcessorService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void process() {

        // Given
        when(processorService.processMessage(any())).thenReturn(Set.of(outputMessage));

        // When
        final Function<ProcessingMessage, List<Message<ProcessingMessage>>> function = processorService.process();

        // Then
        assertNotNull(function);
        final List<Message<ProcessingMessage>> outputList = function.apply(inputMessage);
        assertNotNull(outputList);
        final ProcessingMessage payload = outputList.get(0).getPayload();
        assertEquals(outputMessage.getUid(), payload.getUid());
    }

    @Test
    void process_with_exception() {

        // Given
        when(processorService.processMessage(any())).thenThrow(new ProcessingException("Nope"));

        // When
        final Function<ProcessingMessage, List<Message<ProcessingMessage>>> function = processorService.process();

        // Then
        assertNotNull(function);
        assertThrows(ProcessingException.class, () -> function.apply(inputMessage));
    }

}
