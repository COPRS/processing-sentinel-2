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
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1cPWInputManagementServiceTest extends AbstractTest {

    private L1cPWInputManagementService inputManagementService;

    @Override
    public void setup() throws Exception {
        inputManagementService = new L1cPWInputManagementService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void manageInput() {
        //Given
        final ProcessingMessage processingMessage = ProcessingMessageUtils.create();
        final L1ExecutionInput executionInput = (L1ExecutionInput) new L1ExecutionInput().setDatastrip("FOO");
        processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);
        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {
            final L1ExecutionInput l1ExecutionInput = inputManagementService.manageInput(processingMessage);
            final List<String> logs = logCaptor.getLogs();
            // Then
            assertEquals(2, logs.size());
            assertEquals(executionInput.getDatastrip(), l1ExecutionInput.getDatastrip());
        }
    }

}
