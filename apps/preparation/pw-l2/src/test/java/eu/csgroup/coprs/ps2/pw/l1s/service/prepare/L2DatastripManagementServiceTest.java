package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class L2DatastripManagementServiceTest extends AbstractTest {

    private static final UUID AUX_UID = UUID.randomUUID();
    private static final String TMP_DS_PATH = "/tmp/" + AUX_UID + "/" + L12Parameters.INPUT_FOLDER + "/" + L12Parameters.DS_FOLDER;
    private static final Path dsPath = Paths.get("src/test/resources/datastripManagementServiceTest/S2B_OPER_MSI_L0__DS_REFS_20220629T125610_S20220413T115356_N02.08");
    private static final String DATASTRIP_NAME = "S2B_OPER_MSI_L0__DS_REFS_20220629T125610_S20220413T115356_N02.08";
    private static final Map<String, Boolean> availableByGR = Map.of("foo", true, "bar", false, "foobar", false);
    private static final Set<String> missingTL = Set.of("bar", "foobar");
    private static final Map<String, Boolean> missingAvailableByTL = Map.of("bar", true, "foobar", true);

    @Mock
    private CatalogService catalogService;
    @Mock
    private L2DatastripService datastripService;
    @Mock
    private SharedProperties sharedProperties;
    @Mock
    private ObsService obsService;
    @Mock
    private ObsBucketProperties bucketProperties;

    @InjectMocks
    private L2DatastripManagementService datastripManagementService;

    private L2Datastrip waitingDatastrip;

    @Override
    public void setup() throws Exception {

        waitingDatastrip = new L2Datastrip().setTlComplete(false);
        waitingDatastrip.setAvailableByTL(availableByGR);
        waitingDatastrip.setName(DATASTRIP_NAME);

        datastripManagementService = new L2DatastripManagementService(catalogService, datastripService, sharedProperties, obsService, bucketProperties);
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void updateTLComplete() {
        // Given
        when(datastripService.exists(any())).thenReturn(true);
        when(datastripService.read(any())).thenReturn(waitingDatastrip);
        when(datastripService.update(any())).thenReturn(waitingDatastrip);
        when(bucketProperties.getL1TLBucket()).thenReturn("bucket");
        when(obsService.exists(anyString(), eq(missingTL))).thenReturn(missingAvailableByTL);
        // When
        datastripManagementService.updateTLComplete(DATASTRIP_NAME);
        // Then
        assertTrue(waitingDatastrip.isTlComplete());
    }

    @Test
    void create() throws IOException {
        // Given
        when(sharedProperties.getSharedFolderRoot()).thenReturn("/tmp");
        when(datastripService.create(any(), any(), any(), any(), any(), any())).thenReturn(new L2Datastrip());
        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(AUX_UID);
            doAnswer(invocation -> {
                FileUtils.copyDirectoryToDirectory(dsPath.toFile(), new File(TMP_DS_PATH));
                return null;
            }).when(obsService).download(anySet());
            // When
            datastripManagementService.create(DATASTRIP_NAME, "A", Instant.now(), "s3://path/to/storage");
            // Then
            verify(datastripService).create(any(), any(), any(), any(), any(), any());
        } finally {
            FileSystemUtils.deleteRecursively(Paths.get("/tmp/" + AUX_UID));
        }
    }

}
