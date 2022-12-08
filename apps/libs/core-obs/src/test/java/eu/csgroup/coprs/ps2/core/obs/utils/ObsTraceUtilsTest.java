package eu.csgroup.coprs.ps2.core.obs.utils;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObsTraceUtilsTest extends AbstractTest {

    private static String TMP = "/tmp/obs_test";
    private static String PREFIX = "obs_trace_";
    private static long FILE_SIZE = 10;
    private static int FILE_COUNT = 5;

    private static int WAIT = 2;

    @Override
    public void setup() throws Exception {
        final Path tmpPath = Path.of(TMP);
        FileSystemUtils.deleteRecursively(tmpPath);
        Files.createDirectory(tmpPath);
        TestUtils.createFiles(TMP, PREFIX, FILE_COUNT, FILE_SIZE * 1_024 * 1_024);
    }

    @Override
    public void teardown() throws Exception {
        FileSystemUtils.deleteRecursively(Path.of(TMP));
    }

    @Test
    void traceTransfer() {

        // Given
        final Set<FileInfo> fileInfoSet = IntStream.range(0, FILE_COUNT)
                .mapToObj(i -> new FileInfo().setLocalPath(TMP).setLocalName(PREFIX + i).setObsName(PREFIX + i).setBucket("bucket_" + i))
                .collect(Collectors.toSet());

        // When
        ObsTraceUtils.traceTransfer(fileInfoSet, ReportTask.OBS_READ, UUID.randomUUID(), unused -> {
            try {
                TimeUnit.SECONDS.sleep(WAIT); // NOSONAR
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // Then
        assertTrue(true);
    }

}
