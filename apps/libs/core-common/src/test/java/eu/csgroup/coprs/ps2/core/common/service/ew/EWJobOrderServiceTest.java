package eu.csgroup.coprs.ps2.core.common.service.ew;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.helper.Input;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EWJobOrderServiceTest extends AbstractTest {

    private static final Path rootPath = Paths.get("src/test/resources/ewTest");
    private static final Path folderPath = Paths.get("src/test/resources/ewTest/testJobOrder");
    private static final Map<String, String> jobOrderByName = Map.of(
            "jobOrder1.xml", "foo",
            "jobOrder2.xml", "bar"
    );

    private EWJobOrderService<Input> jobOrderService;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {
        jobOrderService = Mockito.mock(EWJobOrderService.class, Mockito.CALLS_REAL_METHODS);
        FileUtils.deleteQuietly(folderPath.toFile());
        Files.createDirectory(folderPath);
    }

    @Override
    public void teardown() throws Exception {
        FileUtils.deleteQuietly(folderPath.toFile());
    }

    @Test
    void save() throws IOException {

        // When
        jobOrderService.save(jobOrderByName, folderPath);

        // Then
        try (final Stream<Path> pathStream = Files.list(folderPath)) {
            final List<Path> paths = pathStream.toList();
            assertEquals(2, paths.size());
            assertTrue(paths.stream().allMatch(path -> {
                try {
                    return Files.size(path) > 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }

    @Test
    void save_error() {

        // Given
        final Path foo = Paths.get("foo");

        // When Then
        assertThrows(FileOperationException.class, () -> jobOrderService.save(jobOrderByName, foo));
    }

}
