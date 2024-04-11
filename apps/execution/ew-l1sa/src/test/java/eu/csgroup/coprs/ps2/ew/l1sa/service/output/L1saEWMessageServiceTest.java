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

package eu.csgroup.coprs.ps2.ew.l1sa.service.output;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class L1saEWMessageServiceTest extends AbstractTest {

    private L1saEWMessageService messageService;

    @Override
    public void setup() throws Exception {
        messageService = new L1saEWMessageService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getCustomOutputs() {

        // Given
        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(List.of(Path.of("path1"), Path.of("path2")));

            // When
            final Set<String> customOutputs = messageService.getCustomOutputs("folder");

            // Then
            assertEquals(2, customOutputs.size());
        }
    }

}
