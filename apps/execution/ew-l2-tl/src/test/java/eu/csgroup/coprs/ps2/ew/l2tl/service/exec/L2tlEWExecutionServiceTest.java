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

package eu.csgroup.coprs.ps2.ew.l2tl.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class L2tlEWExecutionServiceTest extends AbstractTest {

    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L2tlEWExecutionService executionService;

    @Override
    public void setup() throws Exception {
        executionService = new L2tlEWExecutionService(sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getCommand() {
        // Given
        final L2ExecutionInput executionInput = podamFactory.manufacturePojo(L2ExecutionInput.class);
        when(sharedProperties.getDemFolderRoot()).thenReturn("dem");
        // When
        final List<String> command = executionService.getCommand(executionInput);
        // Then
        assertNotNull(command);
        assertEquals(10, command.size());
    }

    @Test
    void getLevel() {
        assertEquals("L2A_TL", executionService.getLevel());
    }

}
