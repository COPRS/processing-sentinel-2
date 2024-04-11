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

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.ew.model.helper.Input;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EWUploadServiceTest extends AbstractTest {

    private EWUploadService<Input> uploadService;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {
        uploadService = Mockito.mock(EWUploadService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getFileInfoSet() {

        // Given
        final Path path1 = Paths.get("path1.txt").toAbsolutePath();
        final Path path2 = Paths.get("path2.txt");
        String bucket = "bucket";

        // When
        final Set<FileInfo> fileInfoSet = uploadService.getFileInfoSet(List.of(path1, path2), bucket);

        // Then
        assertEquals(2, fileInfoSet.size());
        assertTrue(fileInfoSet.stream().allMatch(fileInfo -> bucket.equals(fileInfo.getBucket())));
    }

}
