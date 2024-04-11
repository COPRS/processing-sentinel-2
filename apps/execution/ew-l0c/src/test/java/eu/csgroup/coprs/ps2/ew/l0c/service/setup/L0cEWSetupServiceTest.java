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

package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class L0cEWSetupServiceTest extends AbstractTest {

    @Mock
    private L0cEWCleanupService cleanupService;
    @Mock
    private L0cEWDownloadService downloadService;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L0cEWSetupService l0cEWSetupService;

    @Override
    public void setup() throws Exception {
        l0cEWSetupService = new L0cEWSetupService(cleanupService, downloadService, sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() {
        // Given
        final L0cExecutionInput executionInput = new L0cExecutionInput();
        when(sharedProperties.getSharedFolderRoot()).thenReturn("foo");
        // When
        l0cEWSetupService.setup(executionInput, null);
        // Then
        verify(cleanupService).cleanAndPrepare("foo");
        verify(downloadService).download(any(), any());
    }

}
