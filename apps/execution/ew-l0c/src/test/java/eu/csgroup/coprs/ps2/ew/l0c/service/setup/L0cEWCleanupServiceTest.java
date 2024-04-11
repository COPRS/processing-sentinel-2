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

package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

class L0cEWCleanupServiceTest extends AbstractTest {

    private L0cEWCleanupService l0cEWCleanupService;

    @Override
    public void setup() throws Exception {
        l0cEWCleanupService = new L0cEWCleanupService(new CleanupProperties());
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void cleanAndPrepare() {

        // Given
        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            // When
            l0cEWCleanupService.cleanAndPrepare("foo");

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()));
        }
    }

    @Test
    void clean() {
        // Given
        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            // When
            l0cEWCleanupService.clean(new L0cExecutionInput().setDtFolder("/path/to/folder"));

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderContent(any()));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolders(any()));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolderIfEmpty(any()));
        }
    }

}
