package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

class L0cEWCleanupServiceTest extends AbstractTest {

    private L0cEWCleanupService l0cEWCleanupService;

    @Override
    public void setup() throws Exception {
        l0cEWCleanupService = new L0cEWCleanupService(new CleanupProperties());
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void cleanAndPrepare() {

        // Given
        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            // When
            l0cEWCleanupService.cleanAndPrepare("foo");

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()));
        }
    }

    @Test
    void clean() {
        // Given
        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            // When
            l0cEWCleanupService.clean(new L0cExecutionInput().setDtFolder("/path/to/folder"));

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolders(any()));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderIfEmpty(any()));
        }
    }

}
