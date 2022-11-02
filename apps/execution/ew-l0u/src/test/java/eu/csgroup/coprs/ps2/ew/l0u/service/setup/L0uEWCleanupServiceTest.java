package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;

class L0uEWCleanupServiceTest extends AbstractTest {

    private L0uEWCleanupService cleanupService;

    @Override
    public void setup() throws Exception {
        cleanupService = new L0uEWCleanupService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void cleanAndPrepare() {
        // Given
        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<ProcessUtils> processUtilsMockedStatic = Mockito.mockStatic(ProcessUtils.class);
        ) {
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);

            // When
            cleanupService.cleanAndPrepare();

            // Then
            processUtilsMockedStatic.verify(() -> ProcessUtils.kill(any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFiles(any(), any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.createFolders(any()), atLeast(1));
        }
    }

    @Test
    void clean() {
        // Given
        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<ProcessUtils> processUtilsMockedStatic = Mockito.mockStatic(ProcessUtils.class);
        ) {
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);

            // When
            cleanupService.clean(null);

            // Then
            processUtilsMockedStatic.verify(() -> ProcessUtils.kill(any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFiles(any(), any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()), atLeast(1));
        }
    }

}
