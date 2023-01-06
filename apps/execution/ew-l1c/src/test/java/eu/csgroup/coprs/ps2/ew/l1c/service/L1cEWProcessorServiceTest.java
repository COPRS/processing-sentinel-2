package eu.csgroup.coprs.ps2.ew.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l1c.service.exec.L1cEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1c.service.output.L1cEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1c.service.setup.L1cEWInputService;
import eu.csgroup.coprs.ps2.ew.l1c.service.setup.L1cEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1cEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L1cEWInputService inputService;
    @Mock
    private L1cEWSetupService setupService;
    @Mock
    private L1cEWExecutionService executionService;
    @Mock
    private L1cEWOutputService outputService;

    @InjectMocks
    private L1cEWProcessorService processorService;

    @Override
    public void setup() throws Exception {
        processorService = new L1cEWProcessorService(inputService, setupService, executionService, outputService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs_TL() {

        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setTile("tile1");

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(1, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(MissingOutputProductType.L1C_TL.getType(), ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getProductMetadataCustomObject().getProductTypeString());
    }

    @Test
    void getMissingOutputs_DS() {

        // Given
        final L1ExecutionInput executionInput = new L1ExecutionInput();

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(1, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(MissingOutputProductType.L1C_DS.getType(), ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getProductMetadataCustomObject().getProductTypeString());
    }

}

