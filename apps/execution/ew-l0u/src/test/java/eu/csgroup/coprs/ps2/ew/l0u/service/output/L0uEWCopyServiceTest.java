package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0uEWCopyServiceTest extends AbstractTest {

    @Mock
    private L0uExecutionProperties l0uExecutionProperties;

    @InjectMocks
    private L0uEWCopyService copyService;

    @Override
    public void setup() throws Exception {
        copyService = new L0uEWCopyService(l0uExecutionProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void copy() {

        // Given
        when(l0uExecutionProperties.getOutputFolderRoot()).thenReturn("foo");

        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
        ) {

            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(List.of(Paths.get("bar")));

            // When
            final String copy = copyService.copy();

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()));
            fileUtilsMockedStatic.verify(() -> FileUtils.copyDirectoryToDirectory(any(), any()));
            assertNotNull(copy);
        }
    }

    @Test
    void copy_error() {

        // Given
        when(l0uExecutionProperties.getOutputFolderRoot()).thenReturn("foo");

        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
        ) {

            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(List.of(Paths.get("bar")));
            fileUtilsMockedStatic.when(() -> FileUtils.copyDirectoryToDirectory(any(), any())).thenThrow(new IOException());

            // When
            assertThrows(FileOperationException.class, () -> copyService.copy());

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()));
            fileUtilsMockedStatic.verify(() -> FileUtils.copyDirectoryToDirectory(any(), any()));
        }
    }

}
