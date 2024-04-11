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

package eu.csgroup.coprs.ps2.ew.l1sb.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

class L1sbEWSetupServiceTest extends AbstractTest {

    @Mock
    private L1sbEWCleanupService cleanupService;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L1sbEWSetupService setupService;

    @Override
    public void setup() throws Exception {
        setupService = new L1sbEWSetupService(cleanupService, sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() {
        // When
        setupService.setup(new L1ExecutionInput(), UUID.randomUUID());
        // Then
        Mockito.verify(cleanupService).cleanAndPrepare(any());
    }

}
