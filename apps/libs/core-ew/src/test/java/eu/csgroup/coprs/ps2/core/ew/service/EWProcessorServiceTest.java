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

package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.exception.ProcessingException;
import eu.csgroup.coprs.ps2.core.common.model.CommonInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.model.helper.Input;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EWProcessorServiceTest extends AbstractTest {

    private static final ProcessingMessage inputMessage = ProcessingMessageUtils.create();

    @Mock
    private EWInputService<Input> inputService;
    @Mock
    private EWSetupService<Input> setupService;
    @Mock
    private EWExecutionService<Input> executionService;
    @Mock
    private EWOutputService<Input> outputService;

    private EWProcessorService<Input> processorService;
    private Set<ProcessingMessage> outputMessageSet;
    private Input input;


    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {

        processorService = Mockito.mock(EWProcessorService.class, Mockito.CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(processorService, "inputService", inputService);
        ReflectionTestUtils.setField(processorService, "setupService", setupService);
        ReflectionTestUtils.setField(processorService, "executionService", executionService);
        ReflectionTestUtils.setField(processorService, "outputService", outputService);

        outputMessageSet = Set.of(ProcessingMessageUtils.create());

        input = podamFactory.manufacturePojo(Input.class);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void processMessage_nominal() {
        // Given
        when(outputService.output(eq(input), any())).thenReturn(outputMessageSet);
        when(inputService.extract(inputMessage)).thenReturn(input);

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final Set<ProcessingMessage> output = processorService.processMessage(inputMessage);

            // Then
            verify(inputService).extract(inputMessage);
            verify(setupService).setup(eq(input), any());
            verify(executionService).execute(eq(input), any());
            verify(outputService).output(eq(input), any());

            assertEquals(outputMessageSet, output);

            assertEquals(2, logCaptor.getLogs().size());
            assertTrue(logCaptor.getLogs().stream().noneMatch(s -> s.contains("ERROR")));
        }
    }

    @Test
    void processMessage_error() {
        // Given
        when(outputService.output(eq(input), any())).thenThrow(new ProcessingException("Nope"));
        when(inputService.extract(inputMessage)).thenReturn(input);

        // When
        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            assertThrows(ProcessingException.class, () -> processorService.processMessage(inputMessage));

            // Then
            verify(inputService).extract(inputMessage);
            verify(setupService).setup(eq(input), any());
            verify(executionService).execute(eq(input), any());
            verify(outputService).output(eq(input), any());

            assertEquals(2, logCaptor.getLogs().size());
            assertEquals(1, logCaptor.getLogs().stream().filter(s -> s.contains("ERROR")).count());
        }
    }

    @Test
    void getTaskOutputs() {

        // Given
        final ProcessingMessage catalogMessage = ProcessingMessageUtils.create().setKeyObjectStorage("foo");
        final ProcessingMessage inputMessage = ProcessingMessageUtils.create();
        inputMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, new CommonInput());

        // When
        final Set<String> taskOutputs = processorService.getTaskOutputs(Set.of(catalogMessage, inputMessage));

        // Then
        assertEquals(1, taskOutputs.size());
    }

}
