package eu.csgroup.coprs.ps2.ew.l0c.service.exec;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;

class L0cEWExecutionServiceTest extends AbstractTest {

    private String folder1 = "/path/to/foo1";
    private String folder2 = "/path/to/foo2";
    private Path path1 = Paths.get(folder1);
    private Path path2 = Paths.get(folder2);
    private List<Path> pathList = List.of(path1, path2);

    private L0cEWExecutionService l0cEWExecutionService;

    @Override
    public void setup() throws Exception {
        l0cEWExecutionService = new L0cEWExecutionService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void execute() {

        // Given
        final UUID parentUid = UUID.randomUUID();
        L0cExecutionInput executionInput = (L0cExecutionInput) new L0cExecutionInput().setSatellite("B");
        try (
                LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class);
                MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<FileContentUtils> fileContentUtilsMockedStatic = Mockito.mockStatic(FileContentUtils.class);
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
        ) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any()))
                    .thenReturn(List.of(path1)) // For DS
                    .thenReturn(pathList);      // For GR
            filesMockedStatic.when(() -> Files.list(any())).thenAnswer(invocation -> Stream.of(path1, path2));

            // When
            l0cEWExecutionService.execute(executionInput, parentUid);
            // Then
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(anySet()), times(9));
            assertEquals(20, logCaptor.getLogs().size());  // 2*9 tasks for satellite B, plus 2 encompassing
        }
    }

}
