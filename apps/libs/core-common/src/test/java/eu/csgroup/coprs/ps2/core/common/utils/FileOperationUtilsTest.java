package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationUtilsTest {

    private static final Path rootPath = Paths.get("src/test/resources/fileOperationUtilsTest/L0U_DUMP").toAbsolutePath();
    private static final Path copyPath = Paths.get("src/test/resources/fileOperationUtilsTest/L0U_DUMP_COPY").toAbsolutePath();

    @BeforeEach
    void setUp() throws IOException {
        if (Files.exists(copyPath)) {
            FileSystemUtils.deleteRecursively(copyPath);
        }
        FileSystemUtils.copyRecursively(rootPath, copyPath);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(copyPath)) {
            FileSystemUtils.deleteRecursively(copyPath);
        }
    }

    @Test
    void deleteFolderContent() throws IOException {
        FileOperationUtils.deleteFolderContent(copyPath.toString());
        assertTrue(FileUtils.isEmptyDirectory(copyPath.toFile()));
    }

    @Test
    void deleteFolderContent_failure() throws IOException {
        assertThrows(FileOperationException.class, () -> FileOperationUtils.deleteFolderContent("moo"));
    }

    @Test
    void deleteFiles() {
        FileOperationUtils.deleteFiles(copyPath.toString(), "^fo.*");
        assertFalse(Files.exists(copyPath.resolve("foo")));
    }

    @Test
    void deleteFiles_failure() {
        assertThrows(FileOperationException.class, () -> FileOperationUtils.deleteFiles("moo", "moo"));
    }

    @Test
    void deleteFolders() throws IOException {

        final Path dir1 = copyPath.resolve("dir1");
        final Path dir2 = copyPath.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        FileOperationUtils.deleteFolders(Set.of(dir1.toString(), dir2.toString()));

        assertFalse(Files.exists(dir1));
        assertFalse(Files.exists(dir2));
    }

    @Test
    void deleteFolderIfEmpty() throws IOException {

        final Path dir1 = copyPath.resolve("dir1");
        Files.createDirectory(dir1);

        FileOperationUtils.deleteFolderIfEmpty(dir1.toString());

        assertFalse(Files.exists(dir1));
    }

    @Test
    void deleteFolderIfEmpty_notEmpty() {

        final Path dt69 = copyPath.resolve("DT69");
        FileOperationUtils.deleteFolderIfEmpty(dt69.toString());

        assertTrue(Files.exists(dt69));
    }

    @Test
    void createFolders() {

        final Path dir1 = copyPath.resolve("dir1");
        final Path dir2 = copyPath.resolve("dir2");

        FileOperationUtils.createFolders(Set.of(dir1.toString(), dir2.toString()));

        assertTrue(Files.exists(dir1));
        assertTrue(Files.exists(dir2));
    }

    @Test
    void findFiles() {
        final List<Path> foo = FileOperationUtils.findFiles(copyPath, "fo*");
        assertEquals(1, foo.size());
    }

    @Test
    void findFolders() {
        final List<Path> folders = FileOperationUtils.findFolders(copyPath, "^DT.*");
        assertEquals(1, folders.size());
    }

    @Test
    void findFoldersInTree() {
        final List<Path> foldersInTree = FileOperationUtils.findFoldersInTree(copyPath, "^S2.*");
        assertEquals(2, foldersInTree.size());
    }

    @Test
    void findDT() {
        final List<Path> folders = FileOperationUtils.findFolders(rootPath, S2FileParameters.DT_REGEX);
        assertEquals(1, folders.size());
    }

    @Test
    void findSAD() {
        final List<Path> folders = FileOperationUtils.findFolders(rootPath, S2FileParameters.SAD_REGEX);
        assertEquals(1, folders.size());
    }

}