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

package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class L0uEWInputServiceTest extends AbstractTest {

    private L0uEWInputService l0uEWInputService;

    private L0uExecutionInput l0uExecutionInput;
    private ProcessingMessage processingMessage;

    @Override
    public void setup() throws Exception {

        l0uEWInputService = new L0uEWInputService();

        processingMessage = ProcessingMessageUtils.create();

        l0uExecutionInput = new L0uExecutionInput().setJobOrders(new HashMap<>(Map.of("foo", "bar")));
        l0uExecutionInput.setFiles(
                Set.of(new FileInfo().setObsName("foo"), new FileInfo().setObsName("bar")));
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void extract() {
        // Given
        processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, l0uExecutionInput);
        // When
        final L0uExecutionInput extract = l0uEWInputService.extract(processingMessage);
        // Then
        assertNotNull(extract);
    }

    @Test
    void extract_bad_input() {
        // Given
        l0uExecutionInput.getJobOrders().put("foo1", "bar1");
        processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, l0uExecutionInput);
        // When Then
        assertThrows(InvalidMessageException.class, () -> l0uEWInputService.extract(processingMessage));
    }

    @Test
    void getTaskInputs() {
        // When
        final Set<String> taskInputs = l0uEWInputService.getTaskInputs(l0uExecutionInput);
        // Then
        assertEquals(2, taskInputs.size());
    }

}
