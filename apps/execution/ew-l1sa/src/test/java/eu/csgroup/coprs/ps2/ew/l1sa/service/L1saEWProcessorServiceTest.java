package eu.csgroup.coprs.ps2.ew.l1sa.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.ew.l1sa.service.exec.L1saEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.output.L1saEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1saEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L1saEWInputService inputService;
    @Mock
    private L1saEWSetupService setupService;
    @Mock
    private L1saEWExecutionService executionService;
    @Mock
    private L1saEWOutputService outputService;

    @InjectMocks
    private L1saEWProcessorService processorService;
    @InjectMocks
    MissingOutputProperties missingOutputProperties;

    @Override
    public void setup() throws Exception {
        processorService = new L1saEWProcessorService(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput()
                .setDatatakeType(DatatakeType.DASC)
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
