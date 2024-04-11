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

package eu.csgroup.coprs.ps2.ew.l0u.service.exec;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


class L0uEWExecutionServiceTest extends AbstractTest {


    private L0uEWExecutionService l0uEWExecutionService;


    @Override
    public void setup() throws Exception {
        l0uEWExecutionService = new L0uEWExecutionService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void execute() {
        // Given
        final UUID parentUid = UUID.randomUUID();
        L0uExecutionInput executionInput = (L0uExecutionInput) new L0uExecutionInput()
                .setJobOrders(Map.of("foo", "bar"))
                .setSatellite("B");
        try (
                LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class);
                MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class);
        ) {
            // When
            l0uEWExecutionService.execute(executionInput, parentUid);
            // Then
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(any(ScriptWrapper.class)));
            assertEquals(4, logCaptor.getLogs().size());
        }
    }

    @Test
    void execute_exit_code_69() {
        // Given
        final UUID parentUid = UUID.randomUUID();
        L0uExecutionInput executionInput = (L0uExecutionInput) new L0uExecutionInput()
                .setJobOrders(Map.of("foo", "bar"))
                .setSatellite("B");
        try (
                LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class);
                MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class);
        ) {
            scriptUtilsMockedStatic.when(() -> ScriptUtils.run(any(ScriptWrapper.class))).thenReturn(69);

            // When
            l0uEWExecutionService.execute(executionInput, parentUid);
            // Then
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(any(ScriptWrapper.class)));
            assertEquals(4, logCaptor.getLogs().size());
            assertEquals(0, logCaptor.getLogs().stream().filter(s -> s.contains("ERROR")).count());
        }
    }

    @Test
    void execute_exit_code_128() {
        // Given
        final UUID parentUid = UUID.randomUUID();
        L0uExecutionInput executionInput = (L0uExecutionInput) new L0uExecutionInput()
                .setJobOrders(Map.of("foo", "bar"))
                .setSatellite("B");
        try (
                LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class);
                MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class);
        ) {
            scriptUtilsMockedStatic.when(() -> ScriptUtils.run(any(ScriptWrapper.class))).thenReturn(128);

            // When
            assertThrows(RuntimeException.class, () -> l0uEWExecutionService.execute(executionInput, parentUid));
            // Then
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(any(ScriptWrapper.class)));
            assertEquals(4, logCaptor.getLogs().size());
            assertEquals(2, logCaptor.getLogs().stream().filter(s -> s.contains("ERROR")).count());
        }
    }

    @Test
    void execute_error() {
        // Given
        final UUID parentUid = UUID.randomUUID();
        L0uExecutionInput executionInput = (L0uExecutionInput) new L0uExecutionInput()
                .setJobOrders(Map.of("foo", "bar"))
                .setSatellite("B");
        try (
                LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class);
                MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class);
        ) {

            scriptUtilsMockedStatic.when(() -> ScriptUtils.run(any(ScriptWrapper.class))).thenThrow(new RuntimeException());

            // When Then
            assertThrows(RuntimeException.class, () -> l0uEWExecutionService.execute(executionInput, parentUid));
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(any(ScriptWrapper.class)));
            assertEquals(4, logCaptor.getLogs().size());
            assertEquals(2, logCaptor.getLogs().stream().filter(s -> s.contains("ERROR")).count());
        }
    }

}
