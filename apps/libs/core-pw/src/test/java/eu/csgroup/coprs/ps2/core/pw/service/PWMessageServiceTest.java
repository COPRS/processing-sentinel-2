package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.pw.model.helper.Input;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PWMessageServiceTest extends AbstractTest {

    private PWMessageService<Input> messageService;

    private List<Input> inputList;

    private ProcessingMessage inputMessage;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {

        messageService = Mockito.mock(PWMessageService.class, Mockito.CALLS_REAL_METHODS);

        inputList = IntStream.of(2).mapToObj(i -> podamFactory.manufacturePojo(Input.class)).toList();

        inputMessage = ProcessingMessageUtils.create();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void build() {
        // When
        final Set<ProcessingMessage> processingMessageSet = messageService.build(inputList, inputMessage);
        // Then
        assertEquals(inputList.size(), processingMessageSet.size());
        assertTrue(processingMessageSet.stream().noneMatch(processingMessage -> processingMessage.getAdditionalFields().isEmpty()));
    }

}
