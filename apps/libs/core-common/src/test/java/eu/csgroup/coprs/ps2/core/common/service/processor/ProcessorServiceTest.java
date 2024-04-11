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

package eu.csgroup.coprs.ps2.core.common.service.processor;

import eu.csgroup.coprs.ps2.core.common.exception.ProcessingException;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProcessorServiceTest extends AbstractTest {

    private static final ProcessingMessage inputMessage = ProcessingMessageUtils.create();
    private static final ProcessingMessage outputMessage = ProcessingMessageUtils.create();

    private ProcessorService processorService;

    @Override
    public void setup() throws Exception {
        processorService = Mockito.mock(ProcessorService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void process() {

        // Given
        when(processorService.processMessage(any())).thenReturn(Set.of(outputMessage));

        // When
        final Function<ProcessingMessage, List<Message<ProcessingMessage>>> function = processorService.process();

        // Then
        assertNotNull(function);
        final List<Message<ProcessingMessage>> outputList = function.apply(inputMessage);
        assertNotNull(outputList);
        final ProcessingMessage payload = outputList.get(0).getPayload();
        assertEquals(outputMessage.getUid(), payload.getUid());
    }

    @Test
    void process_with_exception() {

        // Given
        when(processorService.processMessage(any())).thenThrow(new ProcessingException("Nope"));

        // When
        final Function<ProcessingMessage, List<Message<ProcessingMessage>>> function = processorService.process();

        // Then
        assertNotNull(function);
        assertThrows(ProcessingException.class, () -> function.apply(inputMessage));
    }

}
