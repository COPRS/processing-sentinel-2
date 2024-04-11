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

package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L0cPWExecutionInputServiceTest extends AbstractTest {

    @Mock
    private L0cAuxService auxService;

    @InjectMocks
    private L0cPWExecutionInputService executionInputService;

    @Override
    public void setup() throws Exception {
        executionInputService = new L0cPWExecutionInputService(auxService);
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void create() {

        // Given
        when(auxService.getAux(any())).thenReturn(Map.of(
                AuxProductType.GIP_ATMIMA, List.of(new FileInfo())
        ));

        // When
        final List<L0cExecutionInput> executionInputs = executionInputService.create(List.of(TestHelper.DATASTRIP, TestHelper.UPDATED_DATASTRIP));

        // Then
        verify(auxService, times(2)).getAux(any());
    }

}
