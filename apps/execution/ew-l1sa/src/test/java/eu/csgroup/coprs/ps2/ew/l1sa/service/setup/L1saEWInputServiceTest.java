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

package eu.csgroup.coprs.ps2.ew.l1sa.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1saEWInputServiceTest extends AbstractTest {

    private L1saEWInputService inputService;

    @Override
    public void setup() throws Exception {
        inputService = new L1saEWInputService();
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void getTaskInputs() {
        // Given
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput()
                .setDatastrip("DS")
                .setFiles(Set.of(podamFactory.manufacturePojo(FileInfo.class).setProductFamily(ProductFamily.S2_L0_GR)));
        // When
        final Set<String> taskInputs = inputService.getTaskInputs(executionInput);
        //Then
        assertEquals(2, taskInputs.size());
    }

}
