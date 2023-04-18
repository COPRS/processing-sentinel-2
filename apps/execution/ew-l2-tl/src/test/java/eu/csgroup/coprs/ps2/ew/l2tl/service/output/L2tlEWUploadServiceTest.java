package eu.csgroup.coprs.ps2.ew.l2tl.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ArchiveUtils;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class L2tlEWUploadServiceTest extends AbstractTest {

    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L2tlEWUploadService uploadService;

    @Override
    public void setup() throws Exception {
        uploadService = new L2tlEWUploadService(bucketProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void upload() {
        // Given
        final L2ExecutionInput executionInput = podamFactory.manufacturePojo(L2ExecutionInput.class);
        final List<Path> pathList = List.of(Path.of("/path/to/file1"));

        try (
                MockedStatic<ArchiveUtils> archiveUtilsMockedStatic = Mockito.mockStatic(ArchiveUtils.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
        ) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFiles(any(), any())).thenReturn(Collections.emptyList());
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(pathList);
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);

            // When
            final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = uploadService.upload(executionInput, UUID.randomUUID());

            // Then
            verify(obsService).uploadWithMd5(any(), any());
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFiles(any(), any()), times(2));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()), times(1));
            assertEquals(2, fileInfoByFamily.size());
        }

    }

}
