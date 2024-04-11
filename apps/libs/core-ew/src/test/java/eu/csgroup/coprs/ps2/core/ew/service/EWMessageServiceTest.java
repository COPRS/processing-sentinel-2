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

package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.ew.model.helper.Input;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EWMessageServiceTest extends AbstractTest {

    private EWMessageService<Input> messageService;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {
        messageService = Mockito.mock(EWMessageService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void buildCatalogMessages() {

        // Given
        Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = new HashMap<>();
        fileInfosByFamily.put(
                ProductFamily.S2_AUX,
                Set.of(
                        new FileInfo().setObsURL("s3://bucket/path/aux1"),
                        new FileInfo().setObsURL("s3://bucket/path/aux2")
                ));
        fileInfosByFamily.put(
                ProductFamily.S2_L0_DS,
                Set.of(
                        new FileInfo().setObsURL("s3://bucket/path/ds1"),
                        new FileInfo().setObsURL("s3://bucket/path/ds2"),
                        new FileInfo().setObsURL("s3://bucket/path/ds3")
                ));

        final Input input = podamFactory.manufacturePojo(Input.class);

        // When
        final Set<ProcessingMessage> processingMessages = messageService.buildCatalogMessages(fileInfosByFamily, input);

        // Then
        assertEquals(5, processingMessages.size());
        assertEquals(2,
                processingMessages.stream().filter(processingMessage -> ProductFamily.S2_AUX.equals(processingMessage.getProductFamily())).count()
        );
        assertEquals(3,
                processingMessages.stream().filter(processingMessage -> ProductFamily.S2_L0_DS.equals(processingMessage.getProductFamily())).count()
        );
    }

}
