package eu.csgroup.coprs.ps2.core.common.utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Md5UtilsTest {

    private static final Path rootPath = Paths.get("src/test/resources/md5UtilsTest").toAbsolutePath();
    private static final Path filePath = rootPath.resolve("foo4");

    @Test
    void getFolderMd5() {
        final Map<String, String> md5 = Md5Utils.getMd5(rootPath);
        assertEquals(4, md5.size());
        assertEquals(4, md5.values().stream().distinct().count());
    }

    @Test
    void getSingleFile() {
        final Map<String, String> md5 = Md5Utils.getMd5(filePath);
        assertEquals(1, md5.size());
        assertEquals(1, md5.values().stream().distinct().count());
    }

    @Test
    void getFileMd5() {
        final String foo4 = Md5Utils.getFileMd5(rootPath.resolve("foo4"));
        assertNotNull(foo4);
    }

}
