package eu.csgroup.coprs.ps2.core.common.utils;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileContentUtilsTest {

    private static final Path filePath = Paths.get("src/test/resources/fileContentUtilsTest/foo.txt").toAbsolutePath();
    private static final Path testPath = Paths.get("src/test/resources/fileContentUtilsTest/test.txt").toAbsolutePath();
    private static final Path xmlPath = Paths.get("src/test/resources/fileContentUtilsTest/job_order_eisp_ing_typ.xml").toAbsolutePath();

    @BeforeEach
    void setup() throws IOException {
        FileUtils.deleteQuietly(testPath.toFile());
        Files.copy(filePath, testPath);
    }

    @AfterEach
    void cleanup() {
        FileUtils.deleteQuietly(testPath.toFile());
    }

    @Test
    void xmlToString() {
        final String xmlToString = FileContentUtils.xmlToString(xmlPath.toString());
        assertTrue(xmlToString.contains("<Value>20191208T050815</Value>"));
    }

    @Test
    void grepOne() {
        final Optional<String> grep = FileContentUtils.grepOne(xmlPath, "20191208T050815");
        assertTrue(grep.isPresent());
        assertTrue(grep.get().contains("/Value"));
    }

    @Test
    void grepAll() {
        final List<String> grep = FileContentUtils.grepAll(xmlPath, "<Name>");
        assertFalse(grep.isEmpty());
        assertEquals(13, grep.size());
    }

    @Test
    void grepAll_NotFound() {
        final List<String> grep = FileContentUtils.grepAll(xmlPath, "<Burglop>");
        assertTrue(grep.isEmpty());
    }

    @Test
    void extractXmlTagValue() {
        final String breakpoint_enable = FileContentUtils.extractXmlTagValue(xmlPath, "Breakpoint_Enable");
        assertEquals("true", breakpoint_enable);
    }

    @Test
    void extractValue() {
        final String task_version = FileContentUtils.extractValue(xmlPath, "Task_Version", List.of("^[ ]*", "[ ]$", "^<[^>]*>", "<[^>]*>$"));
        assertEquals("3.0.3", task_version);
    }

    @Test
    void replaceInFile() {
        FileContentUtils.replaceInFile(testPath, Map.of("@l0_gr_count@", "12"));
        assertTrue(FileContentUtils.grepOne(testPath, "12").isPresent());
    }

}
