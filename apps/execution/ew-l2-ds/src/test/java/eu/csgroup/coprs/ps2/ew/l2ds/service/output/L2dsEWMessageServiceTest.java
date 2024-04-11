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

package eu.csgroup.coprs.ps2.ew.l2ds.service.output;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class L2dsEWMessageServiceTest extends AbstractTest {

    @Mock
    private ObsBucketProperties bucketProperties;

    @InjectMocks
    private L2dsEWMessageService messageService;


    @Override
    public void setup() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        messageService = new L2dsEWMessageService(objectMapper, bucketProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void doBuild() {
        // Given
        when(bucketProperties.getL1TLBucket()).thenReturn("bucket");
        final L2ExecutionInput executionInput = podamFactory.manufacturePojo(L2ExecutionInput.class).setTileList(List.of("tile1", "tile2"));
        final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily =
                Map.of(ProductFamily.S2_L2A_DS, Set.of(podamFactory.manufacturePojo(FileInfo.class).setProductFamily(ProductFamily.S2_L2A_DS)));
        // When
        final Set<ProcessingMessage> messages = messageService.doBuild(executionInput, fileInfoByFamily, "output");
        // Then
        assertEquals(3, messages.size());
    }

}
