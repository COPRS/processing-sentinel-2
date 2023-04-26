package eu.csgroup.coprs.ps2.ew.l2ds.service;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.ew.l2ds.service.exec.L2dsEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.output.L2dsEWOutputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWInputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L2dsEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L2dsEWInputService inputService;
    @Mock
    private L2dsEWSetupService setupService;
    @Mock
    private L2dsEWExecutionService executionService;
    @Mock
    private L2dsEWOutputService outputService;

    @InjectMocks
    private L2dsEWProcessorService processorService;
    @InjectMocks
    MissingOutputProperties missingOutputProperties;

    @Override
    public void setup() throws Exception {
        processorService = new L2dsEWProcessorService(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L2ExecutionInput executionInput = new L2ExecutionInput()
                .setTileList(List.of("Tile1", "Tile2"));

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(3, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(2, ((JobProcessingTaskMissingOutput) missingOutputs.get(1)).getEstimatedCountInteger());
    }

}
