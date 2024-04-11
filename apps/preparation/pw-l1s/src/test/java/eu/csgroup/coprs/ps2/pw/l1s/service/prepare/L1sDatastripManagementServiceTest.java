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

package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class L1sDatastripManagementServiceTest extends AbstractTest {

    private static final UUID AUX_UID = UUID.randomUUID();
    private static final String TMP_DS_PATH = "/tmp/" + AUX_UID + "/" + FolderParameters.INPUT_FOLDER + "/" + FolderParameters.DS_FOLDER;
    private static final Map<String, Boolean> availableByGR = Map.of("foo", true, "bar", false, "foobar", false);
    private static final Set<String> missingGR = Set.of("bar", "foobar");
    private static final Map<String, Boolean> missingAvailableByGR = Map.of("bar", true, "foobar", true);

    @Mock
    private CatalogService catalogService;
    @Mock
    private L1sDatastripService datastripService;
    @Mock
    private SharedProperties sharedProperties;
    @Mock
    private ObsService obsService;
    @Mock
    private ObsBucketProperties bucketProperties;

    @InjectMocks
    private L1sDatastripManagementService datastripManagementService;

    private L1sDatastrip waitingDatastrip;

    @Override
    public void setup() throws Exception {

        waitingDatastrip = new L1sDatastrip().setGrComplete(false);
        waitingDatastrip.setAvailableByGR(availableByGR);
        waitingDatastrip.setName(TestHelper.DATASTRIP_NAME);

        datastripManagementService = new L1sDatastripManagementService(catalogService, datastripService, sharedProperties, obsService, bucketProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void updateGRComplete() {
        // Given
        when(datastripService.exists(any())).thenReturn(true);
        when(datastripService.read(any())).thenReturn(waitingDatastrip);
        when(datastripService.update(any())).thenReturn(waitingDatastrip);
        when(bucketProperties.getL0GRBucket()).thenReturn("bucket");
        when(obsService.exists(anyString(), eq(missingGR))).thenReturn(missingAvailableByGR);
        // When
        datastripManagementService.updateGRComplete(TestHelper.DATASTRIP_NAME);
        // Then
        assertTrue(waitingDatastrip.isGrComplete());
    }

    @Test
    void create() throws IOException {
        // Given
        when(sharedProperties.getSharedFolderRoot()).thenReturn("/tmp");
        when(datastripService.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(new L1sDatastrip());
        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(AUX_UID);
            doAnswer(invocation -> {
                FileUtils.copyDirectoryToDirectory(TestHelper.DS_PATH.toFile(), new File(TMP_DS_PATH));
                return null;
            }).when(obsService).download(anySet());
            // When
            datastripManagementService.create(TestHelper.DATASTRIP_NAME, "A", Instant.now(), "s3://path/to/storage");
            // Then
            verify(datastripService).create(any(), any(), any(), any(), any(), any(), any());
        } finally {
            FileSystemUtils.deleteRecursively(Paths.get("/tmp/" + AUX_UID));
        }
    }

}
