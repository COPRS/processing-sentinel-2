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

package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class L1cPWTileManagementServiceTest extends AbstractTest {

    private L1cPWTileManagementService tileManagementService;

    @Override
    public void setup() throws Exception {
        tileManagementService = new L1cPWTileManagementService();
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void listTiles() {
        // Given
        try (
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<FileContentUtils> fileContentUtilsMockedStatic = Mockito.mockStatic(FileContentUtils.class);
        ) {
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            fileContentUtilsMockedStatic.when(() -> FileContentUtils.grepAll(any(), any())).thenReturn(List.of("1", "2", "3", "4"));

            // When
            final Set<String> tiles = tileManagementService.listTiles((L1ExecutionInput) new L1ExecutionInput().setOutputFolder("output"));
            // Then
            assertEquals(4, tiles.size());
        }
    }

}
