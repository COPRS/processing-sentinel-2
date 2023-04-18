package eu.csgroup.coprs.ps2.ew.l2ds.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l2ds.service.exec.L2dsEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.output.L2dsEWOutputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWInputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

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

    @Override
    public void setup() throws Exception {
        processorService = new L2dsEWProcessorService(inputService, setupService, executionService, outputService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L2ExecutionInput executionInput = (L2ExecutionInput) new L2ExecutionInput();

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(1, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
    }

}
