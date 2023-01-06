package eu.csgroup.coprs.ps2.ew.l1c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L1cEWUploadServiceTest extends AbstractTest {

    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L1cEWUploadService uploadService;

    @Override
    public void setup() throws Exception {
        uploadService = new L1cEWUploadService(bucketProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void upload_DS() {
        // Given
        when(bucketProperties.getL1DSBucket()).thenReturn("bucket");
        final L1ExecutionInput executionInput = new L1ExecutionInput();
        final List<Path> pathList = List.of(Path.of("/path/to/file1"), Path.of("/path/to/file2"));

        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
        ) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(pathList);

            // When
            final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = uploadService.upload(executionInput, UUID.randomUUID());

            // Then
            verify(obsService).uploadWithMd5(any(), any());
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()), times(1));
            assertEquals(1, fileInfoByFamily.size());
            fileInfoByFamily.forEach((productFamily, fileInfoSet) -> assertEquals(2, fileInfoSet.size()));
        }

    }

    @Test
    void upload_TL() {
        // Given
        when(bucketProperties.getL1TLBucket()).thenReturn("bucket");
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setTile("tile1");
        final List<Path> pathList = List.of(Path.of("/path/to/file1"), Path.of("/path/to/file2"));

        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
        ) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(pathList);

            // When
            final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = uploadService.upload(executionInput, UUID.randomUUID());

            // Then
            verify(obsService).uploadWithMd5(any(), any());
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()), times(1));
            assertEquals(1, fileInfoByFamily.size());
            fileInfoByFamily.forEach((productFamily, fileInfoSet) -> assertEquals(2, fileInfoSet.size()));
        }

    }

}
