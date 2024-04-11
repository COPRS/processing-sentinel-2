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

package eu.csgroup.coprs.ps2.pw.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cDatastripManagementService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L0cPWInputManagementServiceTest extends AbstractTest {

    private static final String INPUT_FOLDER = "l0u_output";
    private static final String ROOT_FOLDER = "src/test/resources";

    @Mock
    private L0cDatastripManagementService managementService;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L0cPWInputManagementService inputManagementService;

    private ProcessingMessage processingMessage;

    @Override
    public void setup() throws Exception {
        inputManagementService = new L0cPWInputManagementService(managementService, sharedProperties);
        processingMessage = ProcessingMessageUtils.create();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void manageInput() {
        // Given
        L0cPreparationInput l0cExecutionInput = ((L0cPreparationInput) new L0cPreparationInput().setInputFolder(INPUT_FOLDER).setSession("session"));
        processingMessage.getAdditionalFields().put(MessageParameters.PREPARATION_INPUT_FIELD, l0cExecutionInput);
        when(sharedProperties.getSharedFolderRoot()).thenReturn(ROOT_FOLDER);
        // When
        inputManagementService.manageInput(processingMessage);
        // Then
        verify(managementService, times(2)).create(any(), any(), any(), any());
    }

    @Test
    void manageInput_noInput() {
        // Given
        processingMessage = ProcessingMessageUtils.create();
        // When
        inputManagementService.manageInput(processingMessage);
        // Then
        verify(managementService, never()).create(any(), any(), any(), any());
    }

    @Test
    void manageInput_invalidData() {
        // Given
        L0cPreparationInput l0cExecutionInput = new L0cPreparationInput();
        processingMessage.getAdditionalFields().put(MessageParameters.PREPARATION_INPUT_FIELD, l0cExecutionInput);
        // When Then
        assertThrows(InvalidMessageException.class, () -> inputManagementService.manageInput(processingMessage));
    }

}
