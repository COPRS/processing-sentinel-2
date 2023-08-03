package eu.csgroup.coprs.ps2.ew.l1ab.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.ew.l1ab.service.exec.L1abEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.output.L1abEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWInputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class L1abEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L1abEWInputService inputService;
    @Mock
    private L1abEWSetupService setupService;
    @Mock
    private L1abEWExecutionService executionService;
    @Mock
    private L1abEWOutputService outputService;

    @InjectMocks
    private L1abEWProcessorService processorService;
    @InjectMocks
    MissingOutputProperties missingOutputProperties;

    @Override
    public void setup() throws Exception {
        processorService = new L1abEWProcessorService(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setDatatakeType(DatatakeType.RAW).setOutputFolder("folder").setSatellite("B");

        executionInput.setFiles(Set.of(
                new FileInfo().setObsName("file1").setProductFamily(ProductFamily.S2_L0_GR),
                new FileInfo().setObsName("file2").setProductFamily(ProductFamily.S2_L0_GR))
        );


        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(4, missingOutputs.size());
        assertTrue(missingOutputs.stream().allMatch(missingOutput -> missingOutput instanceof JobProcessingTaskMissingOutput));
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(2, ((JobProcessingTaskMissingOutput) missingOutputs.get(1)).getEstimatedCountInteger());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(2)).getEstimatedCountInteger());
        assertEquals(2, ((JobProcessingTaskMissingOutput) missingOutputs.get(3)).getEstimatedCountInteger());

    }

}
