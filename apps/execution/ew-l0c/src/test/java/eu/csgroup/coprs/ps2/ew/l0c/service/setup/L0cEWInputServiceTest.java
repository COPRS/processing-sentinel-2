package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L0cEWInputServiceTest extends AbstractTest {

    private L0cEWInputService l0cEWInputService;

    private final String datastrip = "datastrip";
    private L0cExecutionInput executionInput;
    private ProcessingMessage processingMessage;

    @Override
    public void setup() throws Exception {
        l0cEWInputService = new L0cEWInputService();

        executionInput = (L0cExecutionInput) new L0cExecutionInput().setDatastrip(datastrip).setSatellite("A");
        processingMessage = ProcessingMessageUtils.create();
        processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void extract() {
        // when
        final L0cExecutionInput extract = l0cEWInputService.extract(processingMessage);
        assertEquals(executionInput.getDatastrip(), extract.getDatastrip());
    }

    @Test
    void getTaskInputs() {
        // When
        final Set<String> taskInputs = l0cEWInputService.getTaskInputs(executionInput);
        // Then
        assertEquals(Set.of(datastrip), taskInputs);
    }

}
