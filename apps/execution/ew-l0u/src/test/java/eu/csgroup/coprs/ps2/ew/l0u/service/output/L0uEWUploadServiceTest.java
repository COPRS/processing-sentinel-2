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

package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

class L0uEWUploadServiceTest extends AbstractTest {

    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L0uEWUploadService l0uEWUploadService;

    @Override
    public void setup() throws Exception {
        l0uEWUploadService = new L0uEWUploadService(bucketProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void upload() {

        // Given
        final List<Path> sadPaths = List.of(Paths.get("foo"));
        final List<Path> hktmPaths = List.of(Paths.get("bar"));

        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
             MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);) {

            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(sadPaths);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(hktmPaths);

            // When
            final Map<ProductFamily, Set<FileInfo>> upload = l0uEWUploadService.upload(null, null);

            // Then
            assertEquals(2, upload.size());
        }
    }

    @Test
    void upload_error() {

        // Given
        final List<Path> sadPaths = List.of(Paths.get("foo"));
        final List<Path> hktmPaths = List.of(Paths.get("bar"));

        doThrow(ObsException.class).when(obsService).uploadWithMd5(any(), any());

        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
             MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {

            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(sadPaths);
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(hktmPaths);

            // When Then
            assertThrows(FileOperationException.class, () -> l0uEWUploadService.upload(null, null));
        }
    }

}
