package eu.csgroup.coprs.ps2.core.common.service.processor;

import eu.csgroup.coprs.ps2.core.common.model.helper.Input;
import eu.csgroup.coprs.ps2.core.common.model.helper.Item;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.service.pw.*;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PWProcessorServiceTest extends AbstractTest {

    private static final ProcessingMessage inputMessage = ProcessingMessageUtils.create();

    @Mock
    private PWInputManagementService inputManagementService;
    @Mock
    private PWItemManagementService<Item, PWItemService<Item>> itemManagementService;
    @Mock
    private PWExecutionInputService<Input, Item> executionInputService;
    @Mock
    private PWMessageService<Input> messageService;

    private PWProcessorService<Input, Item, PWItemService<Item>> processorService;
    private List<Item> itemList;
    private List<Input> inputList;
    private Set<ProcessingMessage> outputMessageSet;


    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {

        processorService = Mockito.mock(PWProcessorService.class, Mockito.CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(processorService, "inputManagementService", inputManagementService);
        ReflectionTestUtils.setField(processorService, "itemManagementService", itemManagementService);
        ReflectionTestUtils.setField(processorService, "executionInputService", executionInputService);
        ReflectionTestUtils.setField(processorService, "messageService", messageService);

        itemList = List.of(podamFactory.manufacturePojo(Item.class));
        itemList.forEach(item -> item.setReady(true));
        inputList = List.of(podamFactory.manufacturePojo(Input.class));
        outputMessageSet = Set.of(ProcessingMessageUtils.create());
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void processMessage() {

        // Given
        when(itemManagementService.getReady()).thenReturn(itemList);
        when(executionInputService.create(itemList)).thenReturn(inputList);
        when(messageService.build(inputList)).thenReturn(outputMessageSet);

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final Set<ProcessingMessage> output = processorService.processMessage(inputMessage);

            // Then
            verify(itemManagementService).cleanup();

            verify(itemManagementService).updateAvailableAux();
            verify(itemManagementService).updateNotReady();

            verify(itemManagementService).getReady();

            verify(executionInputService).create(itemList);
            verify(messageService).build(inputList);
            verify(itemManagementService).setJobOrderCreated(itemList);

            assertEquals(2, logCaptor.getLogs().size());

            assertEquals(outputMessageSet, output);
        }
    }

    @Test
    void processMessage_not_ready() {

        // Given
        when(itemManagementService.getReady()).thenReturn(Collections.emptyList());

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final Set<ProcessingMessage> output = processorService.processMessage(inputMessage);

            // Then
            verify(itemManagementService).cleanup();

            verify(itemManagementService).updateAvailableAux();
            verify(itemManagementService).updateNotReady();

            verify(itemManagementService).getReady();

            assertEquals(0, logCaptor.getLogs().size());

            assertTrue(output.isEmpty());
        }
    }

}
