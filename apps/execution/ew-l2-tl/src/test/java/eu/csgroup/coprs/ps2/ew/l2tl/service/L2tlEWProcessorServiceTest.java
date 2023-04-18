package eu.csgroup.coprs.ps2.ew.l2tl.service;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l2tl.service.exec.L2tlEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.output.L2tlEWOutputService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.setup.L2tlEWInputService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.setup.L2tlEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L2tlEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L2tlEWInputService inputService;
    @Mock
    private L2tlEWSetupService setupService;
    @Mock
    private L2tlEWExecutionService executionService;
    @Mock
    private L2tlEWOutputService outputService;

    @InjectMocks
    private L2tlEWProcessorService processorService;

    @Override
    public void setup() throws Exception {
        processorService = new L2tlEWProcessorService(inputService, setupService, executionService, outputService);
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
