package eu.csgroup.coprs.ps2.ew.l1ab.service.output;

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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class L1abEWUploadServiceTest extends AbstractTest {

    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L1abEWUploadService uploadService;

    @Override
    public void setup() throws Exception {
        uploadService = new L1abEWUploadService(bucketProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void upload() {
        // Given
        final L1ExecutionInput executionInput = new L1ExecutionInput();
        final List<Path> pathList = List.of(Path.of("/path/to/file1"), Path.of("/path/to/file2"));

        try (
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
        ) {
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(pathList);

            // When
            final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = uploadService.upload(executionInput, UUID.randomUUID());

            // Then
            verify(obsService).uploadWithMd5(any(), any());
            filesMockedStatic.verify(() -> Files.exists(any()), times(4));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFoldersInTree(any(), any()), times(4));
            assertEquals(4, fileInfoByFamily.size());
            fileInfoByFamily.forEach((productFamily, fileInfoSet) -> assertEquals(2, fileInfoSet.size()));
        }

    }

}
