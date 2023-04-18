package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

class L0cEWUploadServiceTest extends AbstractTest {

    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L0cEWUploadService l0cEWUploadService;

    @Override
    public void setup() throws Exception {
        l0cEWUploadService = new L0cEWUploadService(bucketProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void upload() {

        // Given
        final List<Path> dsPaths = List.of(Paths.get("foo"));
        final List<Path> grPaths = List.of(Paths.get("bar"));

        try (
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
        ) {

            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any()))
                    .thenReturn(dsPaths)
                    .thenReturn(grPaths);

            // When
            final Map<ProductFamily, Set<FileInfo>> upload = l0cEWUploadService.upload(null, null);

            // Then
            assertEquals(2, upload.size());
        }
    }

    @Test
    void upload_error() {

        // Given
        final List<Path> dsPaths = List.of(Paths.get("foo"));
        final List<Path> grPaths = List.of(Paths.get("bar"));

        doThrow(ObsException.class).when(obsService).uploadWithMd5(any(), any());

        try (
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)
        ) {

            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any()))
                    .thenReturn(dsPaths)
                    .thenReturn(grPaths);

            // When Then
            assertThrows(FileOperationException.class, () -> l0cEWUploadService.upload(null, null));
        }
    }

}
