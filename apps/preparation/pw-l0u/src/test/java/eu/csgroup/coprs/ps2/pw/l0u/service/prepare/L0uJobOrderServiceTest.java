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

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.pw.l0u.model.AuxValue;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0uJobOrderServiceTest extends AbstractTest {

    @Mock
    private L0uAuxService auxService;

    @InjectMocks
    private L0uJobOrderService jobOrderService;

    @Override
    public void setup() throws Exception {
        jobOrderService = new L0uJobOrderService(auxService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void create() {
        // Given
        when(auxService.getValues(any(), any(), any())).thenReturn(
                Map.of(
                        AuxValue.GPS_TIME_TAI, "foo",
                        AuxValue.THEORETICAL_LINE_PERIOD, "foo",
                        AuxValue.SP_MIN_SIZE, "foo",
                        AuxValue.TAI_TO_UTC, "foo",
                        AuxValue.ORBIT_OFFSET, "foo",
                        AuxValue.TOTAL_ORBIT, "foo"
                )
        );
        // When
        final Map<String, String> jobOrders = jobOrderService.create(TestHelper.SESSION);
        // Then
        assertEquals(1, jobOrders.size());
        assertTrue(jobOrders.values().stream().allMatch(s -> s.contains("foo")));
    }

}
