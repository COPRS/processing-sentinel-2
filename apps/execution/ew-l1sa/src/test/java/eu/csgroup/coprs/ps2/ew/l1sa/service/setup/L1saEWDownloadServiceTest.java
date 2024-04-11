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

package eu.csgroup.coprs.ps2.ew.l1sa.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ArchiveUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

class L1saEWDownloadServiceTest extends AbstractTest {

    @Mock
    private ObsService obsService;

    @InjectMocks
    private L1saEWDownloadService downloadService;

    @Override
    public void setup() throws Exception {
        downloadService = new L1saEWDownloadService(obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void customAux() {
        // When
        final Predicate<FileInfo> fileInfoPredicate = downloadService.customAux();
        // Then
        assertNotNull(fileInfoPredicate);
    }

    @Test
    void downloadCustomAux() {

        // Given
        final Set<FileInfo> fileInfoSet = Set.of(
                podamFactory.manufacturePojo(FileInfo.class).setAuxProductType(AuxProductType.AUX_CAMSFO),
                podamFactory.manufacturePojo(FileInfo.class).setAuxProductType(AuxProductType.AUX_ECMWFD),
                podamFactory.manufacturePojo(FileInfo.class).setAuxProductType(AuxProductType.GIP_L2ACSC));
        final UUID parentUid = UUID.randomUUID();

        try (
                MockedStatic<ArchiveUtils> archiveUtilsMockedStatic = Mockito.mockStatic(ArchiveUtils.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class);
        ) {

            // When
            downloadService.downloadCustomAux(fileInfoSet, parentUid);

            // Then
            verify(obsService).download(fileInfoSet, parentUid);
        }
    }

}
