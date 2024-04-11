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

package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWCleanupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0cEWOutputServiceTest extends AbstractTest {

    @Mock
    private L0cEWUploadService uploadService;
    @Mock
    private L0cEWMessageService messageService;
    @Mock
    private L0cEWCleanupService cleanupService;

    @InjectMocks
    private L0cEWOutputService l0cEWOutputService;

    @Override
    public void setup() throws Exception {
        l0cEWOutputService = new L0cEWOutputService(messageService, cleanupService, uploadService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void output() {
        // Given
        final Set<ProcessingMessage> messages = Set.of(new ProcessingMessage());
        when(messageService.build(any(), any(), any())).thenReturn(messages);
        // When
        final Set<ProcessingMessage> output = l0cEWOutputService.output(new L0cExecutionInput(), null);
        // Then
        assertEquals(messages, output);
    }

}
