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

package eu.csgroup.coprs.ps2.ew.l1sb.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.ew.l1sb.service.exec.L1sbEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.output.L1sbEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWSetupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1sbEWProcessorServiceTest extends AbstractTest {

    @Mock
    private L1sbEWInputService inputService;
    @Mock
    private L1sbEWSetupService setupService;
    @Mock
    private L1sbEWExecutionService executionService;
    @Mock
    private L1sbEWOutputService outputService;

    @InjectMocks
    private L1sbEWProcessorService processorService;
    @InjectMocks
    MissingOutputProperties missingOutputProperties;

    @Override
    public void setup() throws Exception {
        processorService = new L1sbEWProcessorService(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getMissingOutputs() {

        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput()
                .setDatatakeType(DatatakeType.NOBS)
                .setFiles(Set.of(
                        new FileInfo().setObsName("file1").setProductFamily(ProductFamily.S2_L0_GR),
                        new FileInfo().setObsName("file2").setProductFamily(ProductFamily.S2_L0_GR))
                );

        // When
        final List<TaskMissingOutput> missingOutputs = processorService.getMissingOutputs(executionInput);

        // Then
        assertEquals(5, missingOutputs.size());
        assertEquals(1, ((JobProcessingTaskMissingOutput) missingOutputs.get(0)).getEstimatedCountInteger());
        assertEquals(2, ((JobProcessingTaskMissingOutput) missingOutputs.get(1)).getEstimatedCountInteger());
        assertEquals("MSI_L1C_TL", ((JobProcessingTaskMissingOutput) missingOutputs.get(2)).getProductMetadataCustomObject().getProductTypeString());
    }

}
