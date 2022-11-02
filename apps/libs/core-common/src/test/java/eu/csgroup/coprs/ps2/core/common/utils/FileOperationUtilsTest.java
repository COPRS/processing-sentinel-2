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


    private static final Path ROOT_PATH = Paths.get("src/test/resources/fileOperationUtilsTest").toAbsolutePath();
    private static final Path DUMP_PATH = ROOT_PATH.resolve("L0U_DUMP").toAbsolutePath();
    private static final Path COPY_PATH = ROOT_PATH.resolve("L0U_DUMP_COPY").toAbsolutePath();
    private static final Path MERGE_PATH = ROOT_PATH.resolve("merge").toAbsolutePath();
    private static final Path MERGE_FILE = ROOT_PATH.resolve("S2D_MERGED").toAbsolutePath();

    @BeforeEach
    void setUp() throws IOException {
        if (Files.exists(COPY_PATH)) {
            FileSystemUtils.deleteRecursively(COPY_PATH);
        }
        FileSystemUtils.copyRecursively(DUMP_PATH, COPY_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(COPY_PATH)) {
            FileSystemUtils.deleteRecursively(COPY_PATH);
        }
        if (Files.exists(MERGE_FILE)) {
            FileSystemUtils.deleteRecursively(MERGE_FILE);
        }
    }

    @Test
    void deleteFolderContent() throws IOException {
        FileOperationUtils.deleteFolderContent(COPY_PATH.toString());
        assertTrue(FileUtils.isEmptyDirectory(COPY_PATH.toFile()));
    }

    @Test
    void deleteFolderContent_failure() throws IOException {
        assertThrows(FileOperationException.class, () -> FileOperationUtils.deleteFolderContent("moo"));
    }

    @Test
    void deleteFiles() {
        FileOperationUtils.deleteFiles(COPY_PATH.toString(), "^fo.*");
        assertFalse(Files.exists(COPY_PATH.resolve("foo")));
    }

    @Test
    void deleteFiles_failure() {
        assertThrows(FileOperationException.class, () -> FileOperationUtils.deleteFiles("moo", "moo"));
    }

    @Test
    void deleteFolders() throws IOException {

        final Path dir1 = COPY_PATH.resolve("dir1");
        final Path dir2 = COPY_PATH.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        FileOperationUtils.deleteFolders(Set.of(dir1.toString(), dir2.toString()));

        assertFalse(Files.exists(dir1));
        assertFalse(Files.exists(dir2));
    }

    @Test
    void deleteFolderIfEmpty() throws IOException {

        final Path dir1 = COPY_PATH.resolve("dir1");
        Files.createDirectory(dir1);

        FileOperationUtils.deleteFolderIfEmpty(dir1.toString());

        assertFalse(Files.exists(dir1));
    }

    @Test
    void deleteFolderIfEmpty_notEmpty() {

        final Path dt69 = COPY_PATH.resolve("DT69");
        FileOperationUtils.deleteFolderIfEmpty(dt69.toString());

        assertTrue(Files.exists(dt69));
    }

    @Test
    void createFolders() {

        final Path dir1 = COPY_PATH.resolve("dir1");
        final Path dir2 = COPY_PATH.resolve("dir2");

        FileOperationUtils.createFolders(Set.of(dir1.toString(), dir2.toString()));

        assertTrue(Files.exists(dir1));
        assertTrue(Files.exists(dir2));
    }

    @Test
    void findFiles() {
        final List<Path> foo = FileOperationUtils.findFiles(COPY_PATH, "fo*");
        assertEquals(1, foo.size());
    }

    @Test
    void findFilesInTree() {
        final List<Path> foo = FileOperationUtils.findFilesInTree(COPY_PATH, ".*");
        assertEquals(4, foo.size());
    }

    @Test
    void findFolders() {
        final List<Path> folders = FileOperationUtils.findFolders(COPY_PATH, "^DT.*");
        assertEquals(1, folders.size());
    }

    @Test
    void findFoldersInTree() {
        final List<Path> foldersInTree = FileOperationUtils.findFoldersInTree(COPY_PATH, "^S2.*");
        assertEquals(2, foldersInTree.size());
    }

    @Test
    void findDT() {
        final List<Path> folders = FileOperationUtils.findFolders(DUMP_PATH, S2FileParameters.DT_REGEX);
        assertEquals(1, folders.size());
    }

    @Test
    void findSAD() {
        final List<Path> folders = FileOperationUtils.findFolders(DUMP_PATH, S2FileParameters.SAD_REGEX);
        assertEquals(1, folders.size());
    }

    @Test
    void merge() {
        FileOperationUtils.mergeFiles(MERGE_PATH, MERGE_FILE, "S2D.*");
        assertTrue(Files.exists(MERGE_FILE));
    }

}
