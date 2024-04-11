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

package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.ew.model.helper.Input;
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
