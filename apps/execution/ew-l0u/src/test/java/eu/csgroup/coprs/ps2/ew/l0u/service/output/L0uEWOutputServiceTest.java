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

package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWCleanupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0uEWOutputServiceTest extends AbstractTest {

    @Mock
    private L0uEWUploadService uploadService;
    @Mock
    private L0uEWCopyService copyService;
    @Mock
    private L0uEWMessageService messageService;
    @Mock
    private L0uEWCleanupService cleanupService;

    @InjectMocks
    private L0uEWOutputService l0uEWOutputService;

    @Override
    public void setup() throws Exception {
        l0uEWOutputService = new L0uEWOutputService(messageService, cleanupService, uploadService, copyService);
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
        final Set<ProcessingMessage> output = l0uEWOutputService.output(new L0uExecutionInput(), null);
        // Then
        assertEquals(messages, output);
    }

}
