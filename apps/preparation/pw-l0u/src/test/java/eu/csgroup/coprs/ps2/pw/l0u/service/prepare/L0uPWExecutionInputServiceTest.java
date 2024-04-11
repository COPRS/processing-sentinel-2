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

package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0uPWExecutionInputServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private L0uJobOrderService jobOrderService;
    @Mock
    private ObsBucketProperties bucketProperties;

    @InjectMocks
    private L0uPWExecutionInputService executionInputService;

    @Override
    public void setup() throws Exception {
        executionInputService = new L0uPWExecutionInputService(catalogService, jobOrderService, bucketProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void create() {

        // Given
        when(bucketProperties.getSessionBucket()).thenReturn("bucket");
        when(catalogService.retrieveSessionData(TestHelper.SESSION_NAME)).thenReturn(TestHelper.SESSION_CATALOG_DATA_LIST);
        when(jobOrderService.create(any())).thenReturn(Map.of("JobOrder.xml", "foo"));

        // When
        final List<L0uExecutionInput> executionInputs = executionInputService.create(List.of(TestHelper.SESSION, TestHelper.UPDATED_SESSION));

        // Then
        assertNotNull(executionInputs);
        assertEquals(2, executionInputs.size());
        executionInputs.forEach(
                l0uExecutionInput -> {
                    assertEquals(1, l0uExecutionInput.listJobOrders().size());
                    assertEquals(TestHelper.SESSION_FILES_COUNT, l0uExecutionInput.getFiles().size());
                    assertEquals(TestHelper.SESSION_NAME, l0uExecutionInput.getSession());
                }
        );
    }

}
