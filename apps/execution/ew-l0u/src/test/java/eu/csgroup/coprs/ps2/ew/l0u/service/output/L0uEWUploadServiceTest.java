package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

class L0uEWUploadServiceTest extends AbstractTest {

    @Mock
    private L0uExecutionProperties l0uExecutionProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L0uEWUploadService l0uEWUploadService;

    @Override
    public void setup() throws Exception {
        l0uEWUploadService = new L0uEWUploadService(l0uExecutionProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void upload() {

        // Given
        final List<Path> sadPaths = List.of(Paths.get("foo"));
        final List<Path> hktmPaths = List.of(Paths.get("bar"));

        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(sadPaths);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(hktmPaths);

            // When
            final Map<ProductFamily, Set<FileInfo>> upload = l0uEWUploadService.upload();

            // Then
            assertEquals(2, upload.size());
        }
    }

    @Test
    void upload_error() {

        // Given
        final List<Path> sadPaths = List.of(Paths.get("foo"));
        final List<Path> hktmPaths = List.of(Paths.get("bar"));

        doThrow(ObsException.class).when(obsService).upload(any());

        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(sadPaths);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(hktmPaths);

            // When Then
            assertThrows(FileOperationException.class, () -> l0uEWUploadService.upload());
        }
    }

}
