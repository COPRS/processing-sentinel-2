package eu.csgroup.coprs.ps2.core.obs.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.test.AbstractSpringBootTest;
import eu.csgroup.coprs.ps2.core.common.test.TestUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class S3Tests extends AbstractSpringBootTest { // NOSONAR

    public static final String TMP_DIR = "/tmp/s3tests";
    public static final String SINGLE_FILE_PREFIX = "single_file_";
    public static final String SMALL_FILE_PREFIX = "small_file_";
    public static final String BIG_FILE_PREFIX = "big_file_";

    @Autowired
    private ObsService obsService;
    @Autowired
    private ObsBucketProperties bucketProperties;


    @Override
    public void setup() throws Exception {
        final Path tmpPath = Path.of(TMP_DIR);
        FileSystemUtils.deleteRecursively(tmpPath);
        Files.createDirectory(tmpPath);
    }

    @Override
    public void teardown() throws Exception {
        FileSystemUtils.deleteRecursively(Path.of(TMP_DIR));
    }

    // @Test
    @Order(1)
    void uploadSmallFile() throws IOException {
        // Given
        Set<FileInfo> fileInfoSet = createFilesAndFileInfo(bucketProperties.getAuxBucket(), TMP_DIR, SINGLE_FILE_PREFIX, 1, 100);
        // When
        obsService.upload(fileInfoSet);
        // Then
        fileInfoSet.forEach(fileInfo ->
                Assertions.assertTrue(obsService.exists(bucketProperties.getAuxBucket(), fileInfo.getKey()))
        );
    }

    // @Test
    @Order(2)
    void uploadBigFile() throws IOException {
        // Given
        Set<FileInfo> fileInfoSet = createFilesAndFileInfo(bucketProperties.getAuxBucket(), TMP_DIR, BIG_FILE_PREFIX, 1, 1_100_000_000);
        // When
        obsService.upload(fileInfoSet);
        // Then
        fileInfoSet.forEach(fileInfo ->
                Assertions.assertTrue(obsService.exists(bucketProperties.getAuxBucket(), fileInfo.getKey()))
        );
    }

    // @Test
    @Order(3)
    void uploadManyFiles() throws IOException {
        // Given
        Set<FileInfo> fileInfoSet = createFilesAndFileInfo(bucketProperties.getAuxBucket(), TMP_DIR, SMALL_FILE_PREFIX, 1_000, 100);
        // When
        obsService.upload(fileInfoSet);
        // Then
        fileInfoSet.forEach(fileInfo ->
                Assertions.assertTrue(obsService.exists(bucketProperties.getAuxBucket(), fileInfo.getKey()))
        );
    }

    // @Test
    @Order(4)
    void downloadManyFiles() {
        // Given
        Set<FileInfo> fileInfoSet = IntStream.range(0, 1000)
                .mapToObj(value -> {
                    String key = SMALL_FILE_PREFIX + String.format("%04d", value);
                    Path path = Path.of(TMP_DIR, key);
                    return new FileInfo().setBucket(bucketProperties.getAuxBucket()).setKey(key).setFullLocalPath(path.toString()).setSimpleFile(true);
                })
                .collect(Collectors.toSet());
        // When
        obsService.download(fileInfoSet);
        // Then
        Assertions.assertEquals(1000, FileOperationUtils.findFiles(Path.of(TMP_DIR), SMALL_FILE_PREFIX + ".*").size());
    }

    // @Test
    @Order(5)
    void downloadBigFile() {
        // Given
        String key = BIG_FILE_PREFIX + "0";
        Path path = Path.of(TMP_DIR, key);
        Set<FileInfo> fileInfoSet = Set.of(new FileInfo().setBucket(bucketProperties.getAuxBucket()).setKey(key).setFullLocalPath(path.toString()).setSimpleFile(true));
        // When
        obsService.download(fileInfoSet);
        // Then
        Assertions.assertTrue(Files.exists(path));
    }

    // @Test
    @Order(6)
    void uploadFolder() throws IOException {
        // Given
        TestUtils.createFiles(TMP_DIR, SMALL_FILE_PREFIX, 1000, 100);
        final String folderKey = StringUtils.substringAfterLast(TMP_DIR, "/");
        // When
        obsService.upload(Set.of(new FileInfo().setBucket(bucketProperties.getAuxBucket()).setKey(folderKey).setFullLocalPath(TMP_DIR)));
        // Then
        Assertions.assertTrue(obsService.exists(bucketProperties.getAuxBucket(), folderKey));
    }

    // @Test
    @Order(7)
    void downloadFolder() {
        // Given
        final String folderKey = StringUtils.substringAfterLast(TMP_DIR, "/");
        // When
        obsService.download(Set.of(new FileInfo().setBucket(bucketProperties.getAuxBucket()).setKey(folderKey).setFullLocalPath(TMP_DIR)));
        // Then
        Assertions.assertEquals(1000, FileOperationUtils.findFiles(Path.of(TMP_DIR), SMALL_FILE_PREFIX + ".*").size());
    }

    private Set<FileInfo> createFilesAndFileInfo(String bucket, String rootPath, String prefix, int count, long size) throws IOException {

        final Set<Path> files = TestUtils.createFiles(rootPath, prefix, count, size);

        return files
                .stream()
                .map(
                        file -> new FileInfo()
                                .setBucket(bucket)
                                .setKey(StringUtils.substringAfterLast(file.toString(), "/"))
                                .setFullLocalPath(file.toString()))
                .collect(Collectors.toSet());
    }

}
