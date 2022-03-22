package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.settings.FileParameters;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileOperationUtilsTest {

    private static final Path rootPath = Paths.get("src/test/resources/fileOperationUtilsTest/L0U_DUMP").toAbsolutePath();

    @Test
    void findDT() {
        final List<Path> folders = FileOperationUtils.findFolders(rootPath, FileParameters.DT_REGEX);
        assertEquals(1, folders.size());
    }

    @Test
    void findSAD() {
        final List<Path> folders = FileOperationUtils.findFolders(rootPath, FileParameters.SAD_REGEX);
        assertEquals(1, folders.size());
    }

}