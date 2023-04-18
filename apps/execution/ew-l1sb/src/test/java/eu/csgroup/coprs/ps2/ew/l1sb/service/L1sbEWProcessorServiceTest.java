package eu.csgroup.coprs.ps2.ew.l1sb.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l1sb.service.exec.L1sbEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.output.L1sbEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class L1sbEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L1sbEWInputService inputService;
    @Mock
    private L1sbEWSetupService setupService;
    @Mock
    private L1sbEWExecutionService executionService;
    @Mock
    private L1sbEWOutputService outputService;

    @InjectMocks
    private L1sbEWProcessorService processorService;

    @Override
    public void setup() throws Exception {
        processorService = new L1sbEWProcessorService(inputService, setupService, executionService, outputService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput()
                .setFiles(Set.of(
                        new FileInfo().setObsName("file1").setProductFamily(ProductFamily.S2_L0_GR),
                        new FileInfo().setObsName("file2").setProductFamily(ProductFamily.S2_L0_GR))
                );

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(2, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(2, ((JobProcessingTaskMissingOutput) missingOutputs.get(1)).getEstimatedCountInteger());
    }
    
}
