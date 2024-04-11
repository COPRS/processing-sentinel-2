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

package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L2AuxServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L2AuxService auxService;

    @Override
    public void setup() throws Exception {
        auxService = new L2AuxService(catalogService, bucketProperties, sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getAux() {

        // Given
        final AuxCatalogData auxCatalogData = (AuxCatalogData) new AuxCatalogData().setProductName("foo").setKeyObjectStorage("bar");
        when(catalogService.retrieveLatestAuxData(any(), any(), any(), any(), any())).thenReturn(Optional.of(auxCatalogData));
        when(sharedProperties.getSharedFolderRoot()).thenReturn("/shared");

        // When
        final Map<AuxProductType, List<FileInfo>> filesByAux = auxService.getAux(TestHelper.UPDATED_DATASTRIP);

        // Then
        assertEquals(6, filesByAux.size());
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getKey().equals("bar")));
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getLocalName().equals("foo")));
    }

}
