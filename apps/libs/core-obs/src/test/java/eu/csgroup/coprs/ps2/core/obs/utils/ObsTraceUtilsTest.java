/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
