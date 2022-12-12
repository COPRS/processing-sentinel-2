package eu.csgroup.coprs.ps2.ew.l1sa.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1saEWInputServiceTest extends AbstractTest {

    private L1saEWInputService inputService;

    @Override
    public void setup() throws Exception {
        inputService = new L1saEWInputService();
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void getTaskInputs() {
        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput()
                .setDatastrip("DS")
                .setFiles(Set.of(podamFactory.manufacturePojo(FileInfo.class).setProductFamily(ProductFamily.S2_L0_GR)));
        // When
        final Set<String> taskInputs = inputService.getTaskInputs(executionInput);
        //Then
        assertEquals(2, taskInputs.size());
    }

}
