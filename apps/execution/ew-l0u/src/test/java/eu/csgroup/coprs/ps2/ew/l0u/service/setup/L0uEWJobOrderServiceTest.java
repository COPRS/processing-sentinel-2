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

package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

class L0uEWJobOrderServiceTest extends AbstractTest {

    private L0uEWJobOrderService l0uEWJobOrderService;

    @Override
    public void setup() throws Exception {
        l0uEWJobOrderService = new L0uEWJobOrderService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void saveJobOrders() {
        // Given
        try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
            // When
            l0uEWJobOrderService.saveJobOrders(new L0uExecutionInput().setJobOrders(Map.of("foo", "bar")));
            // Then
            filesMockedStatic.verify(() -> Files.writeString(any(), any()));
        }
    }

}
