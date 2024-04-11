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

package eu.csgroup.coprs.ps2.ew.l2ds.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ArchiveUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class L2dsEWUploadServiceTest extends AbstractTest {

    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L2dsEWUploadService uploadService;

    @Override
    public void setup() throws Exception {
        uploadService = new L2dsEWUploadService(bucketProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void upload() {
        // Given
        final L2ExecutionInput executionInput = podamFactory.manufacturePojo(L2ExecutionInput.class);
        final List<Path> pathList = List.of(Path.of("/path/to/file1"));

        try (
                MockedStatic<ArchiveUtils> archiveUtilsMockedStatic = Mockito.mockStatic(ArchiveUtils.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
        ) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFiles(any(), any())).thenReturn(Collections.emptyList());
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(pathList);
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            // When
            final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = uploadService.upload(executionInput, UUID.randomUUID());

            // Then
            verify(obsService).uploadWithMd5(any(), any());
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFiles(any(), any()), times(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()), times(1));
            assertEquals(1, fileInfoByFamily.size());
            fileInfoByFamily.forEach((productFamily, fileInfoSet) -> assertEquals(1, fileInfoSet.size()));
        }

    }

}
