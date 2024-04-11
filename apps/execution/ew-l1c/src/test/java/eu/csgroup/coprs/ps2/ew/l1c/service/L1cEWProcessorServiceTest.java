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

package eu.csgroup.coprs.ps2.ew.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.ew.l1c.service.exec.L1cEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1c.service.output.L1cEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1c.service.setup.L1cEWInputService;
import eu.csgroup.coprs.ps2.ew.l1c.service.setup.L1cEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1cEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L1cEWInputService inputService;
    @Mock
    private L1cEWSetupService setupService;
    @Mock
    private L1cEWExecutionService executionService;
    @Mock
    private L1cEWOutputService outputService;

    @InjectMocks
    private L1cEWProcessorService processorService;
    @InjectMocks
    MissingOutputProperties missingOutputProperties;

    @Override
    public void setup() throws Exception {
        processorService = new L1cEWProcessorService(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs_TL() {

        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setTile("tile1");

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(2, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(MissingOutputProductType.L1C_TL.getType(), ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getProductMetadataCustomObject().getProductTypeString());
        assertEquals(MissingOutputProductType.L1C_TC.getType(), ((JobProcessingTaskMissingOutput) missingOutputs.get(1)).getProductMetadataCustomObject().getProductTypeString());
    }

    @Test
    void getMissingOutputs_DS() {

        // Given
        final L1ExecutionInput executionInput = new L1ExecutionInput();

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(1, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(MissingOutputProductType.L1C_DS.getType(), ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getProductMetadataCustomObject().getProductTypeString());
    }

}

