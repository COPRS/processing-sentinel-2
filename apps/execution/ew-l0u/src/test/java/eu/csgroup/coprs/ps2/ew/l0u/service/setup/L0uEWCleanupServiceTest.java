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

package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;

class L0uEWCleanupServiceTest extends AbstractTest {

    private L0uEWCleanupService cleanupService;

    @Override
    public void setup() throws Exception {
        cleanupService = new L0uEWCleanupService(new CleanupProperties());
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void cleanAndPrepare() {
        // Given
        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<ProcessUtils> processUtilsMockedStatic = Mockito.mockStatic(ProcessUtils.class);
        ) {
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);

            // When
            cleanupService.cleanAndPrepare("foo");

            // Then
            processUtilsMockedStatic.verify(() -> ProcessUtils.kill(any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFiles(any(), any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.createFolders(any()), atLeast(1));
        }
    }

    @Test
    void clean() {
        // Given
        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<ProcessUtils> processUtilsMockedStatic = Mockito.mockStatic(ProcessUtils.class);
        ) {
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);

            // When
            cleanupService.clean(null);

            // Then
            processUtilsMockedStatic.verify(() -> ProcessUtils.kill(any()), atLeast(1));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()), atLeast(1));
        }
    }

}
