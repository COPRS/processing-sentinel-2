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

package eu.csgroup.coprs.ps2.ew.l0c.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class L0cEWExecutionServiceTest extends AbstractTest {

    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L0cEWExecutionService executionService;

    @Override
    public void setup() throws Exception {
        executionService = new L0cEWExecutionService(sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void processing() {

        // Given
        final L0cExecutionInput executionInput = (L0cExecutionInput) new L0cExecutionInput().setAuxFolder("foo").setInputFolder("foo").setOutputFolder("foo");
        when(sharedProperties.getMaxParallelTasks()).thenReturn(1);
        when(sharedProperties.getDemFolderRoot()).thenReturn("foo");
        when(sharedProperties.getGridFolderRoot()).thenReturn("bar");

        try (
                MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class);
                MockedStatic<FileContentUtils> fileContentUtilsMockedStatic = Mockito.mockStatic(FileContentUtils.class);
        ) {
            fileContentUtilsMockedStatic.when(() -> FileContentUtils.grepAll(any(), any())).thenReturn(Collections.emptyList());

            // When
            executionService.execute(executionInput, UUID.randomUUID());

            // Then
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(any(ScriptWrapper.class)), times(3));
        }
    }

    @Test
    void getLevel() {
        assertEquals("L0c", executionService.getLevel());
    }

}
