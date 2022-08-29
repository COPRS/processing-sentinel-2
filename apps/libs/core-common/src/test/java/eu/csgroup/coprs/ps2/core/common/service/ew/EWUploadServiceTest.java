package eu.csgroup.coprs.ps2.core.common.service.ew;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EWUploadServiceTest extends AbstractTest {

    private EWUploadService uploadService;

    @Override
    public void setup() throws Exception {
        uploadService = Mockito.mock(EWUploadService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getFileInfoSet() {

        // Given
        final Path path1 = Paths.get("path1.txt").toAbsolutePath();
        final Path path2 = Paths.get("path2.txt");
        String bucket = "bucket";

        // When
        final Set<FileInfo> fileInfoSet = uploadService.getFileInfoSet(List.of(path1, path2), bucket);

        // Then
        assertEquals(2, fileInfoSet.size());
        assertTrue(fileInfoSet.stream().allMatch(fileInfo -> bucket.equals(fileInfo.getBucket())));
    }

}
