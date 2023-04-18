package eu.csgroup.coprs.ps2.ew.l1c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1cEWInputServiceTest extends AbstractTest {

    private L1cEWInputService inputService;

    @Override
    public void setup() throws Exception {
        inputService = new L1cEWInputService();
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void getTaskInputs() {
        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setCustomTaskInputs(Set.of("L1B", "L1C"));
        // When
        final Set<String> taskInputs = inputService.getTaskInputs(executionInput);
        //Then
        assertEquals(1, taskInputs.size());
    }

}
