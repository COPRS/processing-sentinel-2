package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.ExtractionException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ArchiveUtilsTest {

    private static final Path testFolderPath = Paths.get("src/test/resources/archiveTestFiles_tmp");
    private static final Path testFilesPath = Paths.get("src/test/resources/archiveTestFiles");

    private static final Path tarPath = testFilesPath.resolve("tarFile.tar");
    private static final Path tarGzPath = testFilesPath.resolve("tarGzFile.tar.gz");
    private static final Path tgzPath = testFilesPath.resolve("tgzFile.tgz");
    private static final Path zipPath = testFilesPath.resolve("zipFile.zip");

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(testFolderPath);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(testFolderPath);
    }

    @Test
    void unTarGz_noDelete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tarGzPath);
        // When
        ArchiveUtils.unTarGz(filePath.toString(), false);
        // Then
        List<Path> files = getTestFolderContent(16);
        assertTrue(files.contains(filePath));
        assertFalse(files.contains(Paths.get(StringUtils.removeEnd(filePath.toString(), ".gz"))));
    }

    @Test
    void unTarGz_delete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tarGzPath);
        // When
        ArchiveUtils.unTarGz(filePath.toString(), true);
        // Then
        List<Path> files = getTestFolderContent(15);
        assertFalse(files.contains(filePath));
        assertFalse(files.contains(Paths.get(StringUtils.removeEnd(filePath.toString(), ".gz"))));
    }

    @Test
    void unTarGz_tgz() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tgzPath);
        // When
        ArchiveUtils.unTarGz(filePath.toString(), true);
        // Then
        List<Path> files = getTestFolderContent(15);
        assertFalse(files.contains(filePath));
        assertFalse(files.contains(Paths.get(StringUtils.replace(filePath.toString(), ".tgz", ".tar"))));
    }

    @Test
    void unTar_noDelete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tarPath);
        // When
        ArchiveUtils.unTar(filePath.toString(), false);
        // Then
        List<Path> files = getTestFolderContent(16);
        assertTrue(files.contains(filePath));
    }

    @Test
    void unTar_delete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tarPath);
        // When
        ArchiveUtils.unTar(filePath.toString(), true);
        // Then
        List<Path> files = getTestFolderContent(15);
        assertFalse(files.contains(filePath));
    }

    @Test
    void unZip_noDelete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(zipPath);
        // When
        ArchiveUtils.unZip(filePath.toString(), false);
        // Then
        List<Path> files = getTestFolderContent(16);
        assertTrue(files.contains(filePath));
    }

    @Test
    void unZip_delete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(zipPath);
        // When
        ArchiveUtils.unZip(filePath.toString(), true);
        // Then
        List<Path> files = getTestFolderContent(15);
        assertFalse(files.contains(filePath));
    }

    @Test
    void unGzip_noDelete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tarGzPath);
        // When
        ArchiveUtils.unGzip(filePath.toString(), false);
        // Then
        List<Path> files = getTestFolderContent(2);
        assertTrue(files.contains(filePath));
        assertTrue(files.contains(Paths.get(StringUtils.removeEnd(filePath.toString(), ".gz"))));
    }

    @Test
    void unGzip_delete() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tarGzPath);
        // When
        ArchiveUtils.unGzip(filePath.toString(), true);
        // Then
        List<Path> files = getTestFolderContent(1);
        assertFalse(files.contains(filePath));
        assertTrue(files.contains(Paths.get(StringUtils.removeEnd(filePath.toString(), ".gz"))));
    }

    @Test
    void unGzip_tgz() throws IOException, ExtractionException {
        // Given
        Path filePath = prepareFile(tgzPath);
        // When
        ArchiveUtils.unGzip(filePath.toString(), true);
        // Then
        List<Path> files = getTestFolderContent(1);
        assertFalse(files.contains(filePath));
        assertTrue(files.contains(Paths.get(StringUtils.replace(filePath.toString(), ".tgz", ".tar"))));
    }

    private Path prepareFile(Path path) throws IOException {
        Path filePath = testFolderPath.resolve(path.getFileName());
        Files.copy(path, filePath);
        return filePath;

    }

    private List<Path> getTestFolderContent(int size) throws IOException {
        List<Path> files = Files.walk(testFolderPath)
                .filter(path -> !path.equals(testFolderPath))
                .collect(Collectors.toList());
        assertEquals(size, files.size());
        return files;
    }

}