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

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L0uEWCopyServiceTest extends AbstractTest {

    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L0uEWCopyService copyService;

    @Override
    public void setup() throws Exception {
        copyService = new L0uEWCopyService(sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void copy() {

        // Given
        when(sharedProperties.getSharedFolderRoot()).thenReturn("foo");

        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
        ) {

            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(List.of(Paths.get("bar")));

            // When
            final String copy = copyService.copy();

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()));
            fileUtilsMockedStatic.verify(() -> FileUtils.copyDirectoryToDirectory(any(), any()));
            assertNotNull(copy);
        }
    }

    @Test
    void copy_error() {

        // Given
        when(sharedProperties.getSharedFolderRoot()).thenReturn("foo");

        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
        ) {

            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(List.of(Paths.get("bar")));
            fileUtilsMockedStatic.when(() -> FileUtils.copyDirectoryToDirectory(any(), any())).thenThrow(new IOException());

            // When
            assertThrows(FileOperationException.class, () -> copyService.copy());

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.findFolders(any(), any()));
            fileUtilsMockedStatic.verify(() -> FileUtils.copyDirectoryToDirectory(any(), any()));
        }
    }

    @Test
    void copy_whenNoDataStripFound_ReturnNull() {

        // Given
        try (
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
                MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
        ) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFolders(any(), any())).thenReturn(Collections.emptyList());

            //When
            String result = copyService.copy();

            //then
            assertNull(result);
            fileUtilsMockedStatic.verify(() -> FileUtils.copyDirectoryToDirectory(any(), any()), never());
            verify(sharedProperties, never()).getSharedFolderRoot();
        }
    }

}
