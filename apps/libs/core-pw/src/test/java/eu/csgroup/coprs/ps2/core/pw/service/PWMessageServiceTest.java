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

package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.pw.model.helper.Input;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PWMessageServiceTest extends AbstractTest {

    private PWMessageService<Input> messageService;

    private List<Input> inputList;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {

        messageService = Mockito.mock(PWMessageService.class, Mockito.CALLS_REAL_METHODS);

        inputList = IntStream.of(2).mapToObj(i -> podamFactory.manufacturePojo(Input.class)).toList();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void build() {
        // When
        final Set<ProcessingMessage> processingMessageSet = messageService.build(inputList);
        // Then
        assertEquals(inputList.size(), processingMessageSet.size());
        assertTrue(processingMessageSet.stream().noneMatch(processingMessage -> processingMessage.getAdditionalFields().isEmpty()));
    }

}
