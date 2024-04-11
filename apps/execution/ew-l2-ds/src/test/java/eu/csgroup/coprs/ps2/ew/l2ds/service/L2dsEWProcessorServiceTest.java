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

package eu.csgroup.coprs.ps2.ew.l2ds.service;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.ew.l2ds.service.exec.L2dsEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.output.L2dsEWOutputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWInputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L2dsEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L2dsEWInputService inputService;
    @Mock
    private L2dsEWSetupService setupService;
    @Mock
    private L2dsEWExecutionService executionService;
    @Mock
    private L2dsEWOutputService outputService;

    @InjectMocks
    private L2dsEWProcessorService processorService;
    @InjectMocks
    MissingOutputProperties missingOutputProperties;

    @Override
    public void setup() throws Exception {
        processorService = new L2dsEWProcessorService(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L2ExecutionInput executionInput = new L2ExecutionInput()
                .setTileList(List.of("Tile1", "Tile2"));

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(3, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(2, ((JobProcessingTaskMissingOutput) missingOutputs.get(1)).getEstimatedCountInteger());
    }

}
