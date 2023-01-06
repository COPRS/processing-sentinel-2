package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1cPWInputManagementServiceTest extends AbstractTest {

    private L1cPWInputManagementService inputManagementService;

    @Override
    public void setup() throws Exception {
        inputManagementService = new L1cPWInputManagementService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void manageInput() {
        //Given
        final ProcessingMessage processingMessage = ProcessingMessageUtils.create();
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setDatastrip("FOO");
        processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);
        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {
            final L1ExecutionInput l1ExecutionInput = inputManagementService.manageInput(processingMessage);
            final List<String> logs = logCaptor.getLogs();
            // Then
            assertEquals(2, logs.size());
            assertEquals(executionInput.getDatastrip(), l1ExecutionInput.getDatastrip());
        }
    }

}
