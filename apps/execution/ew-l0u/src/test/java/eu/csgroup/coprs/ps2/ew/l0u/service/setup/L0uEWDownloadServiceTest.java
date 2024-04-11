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

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class L0uEWDownloadServiceTest extends AbstractTest {

    @Mock
    private ObsService obsService;

    @InjectMocks
    private L0uEWDownloadService l0uEWDownloadService;

    @Override
    public void setup() throws Exception {
        l0uEWDownloadService = new L0uEWDownloadService(obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void download() {

        // Given
        final Set<FileInfo> fileInfoSet = Set.of(
                new FileInfo().setProductFamily(ProductFamily.EDRS_SESSION).setObsName("ch1_foo"),
                new FileInfo().setProductFamily(ProductFamily.EDRS_SESSION).setObsName("ch2_foo")
        );

        // When
        l0uEWDownloadService.download(fileInfoSet, null);

        // Then
        verify(obsService).download(fileInfoSet, null);
        assertTrue(fileInfoSet.stream().allMatch(fileInfo -> fileInfo.getLocalPath().contains("/ch_")));
    }

}
