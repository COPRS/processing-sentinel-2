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

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class L0cEWDownloadServiceTest extends AbstractTest {

    @Mock
    private ObsService obsService;

    private L0cEWDownloadService l0cEWDownloadService;

    @Override
    public void setup() throws Exception {
        l0cEWDownloadService = new L0cEWDownloadService(obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void download() {
        // Given
        final Set<FileInfo> fileInfoSet = Set.of(
                new FileInfo().setObsName("file1").setProductFamily(ProductFamily.S2_AUX).setAuxProductType(AuxProductType.AUX_UT1UTC).setFullLocalPath("/path/to/file1"),
                new FileInfo().setObsName("file2").setProductFamily(ProductFamily.S2_AUX).setAuxProductType(AuxProductType.GIP_ATMIMA).setFullLocalPath("/path/to/file2")
        );

        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            // When
            l0cEWDownloadService.download(fileInfoSet, null);

            // Then
            verify(obsService).download(fileInfoSet, null);
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.move(any(), any()), times(2));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolders(any()));
        }
    }

}
