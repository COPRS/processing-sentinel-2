package eu.csgroup.coprs.ps2.ew.l1ab.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.ew.l1ab.service.exec.L1abEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.output.L1abEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWInputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

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

    @Override
    public void setup() throws Exception {
        processorService = new L1abEWProcessorService(inputService, setupService, executionService, outputService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setDatatakeType(DatatakeType.RAW).setOutputFolder("folder").setSatellite("B");
        final List<Path> pathList = List.of(Path.of("foo"), Path.of("bar"));

        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(pathList);

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

}
