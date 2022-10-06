package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l1s.config.L1sPreparationProperties;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class L1sDatastripManagementServiceTest extends AbstractTest {

    private static final UUID AUX_UID = UUID.randomUUID();
    private static final String TMP_DS_PATH = "/tmp/" + AUX_UID + "/" + L1Parameters.INPUT_FOLDER + "/" + L1Parameters.DS_FOLDER;
    private static final Path dsPath = Paths.get("src/test/resources/datastripManagementServiceTest/S2B_OPER_MSI_L0__DS_REFS_20220629T125610_S20220413T115356_N02.08");
    private static final String DATASTRIP_NAME = "S2B_OPER_MSI_L0__DS_REFS_20220629T125610_S20220413T115356_N02.08";
    private static final Map<String, Boolean> availableByGR = Map.of("foo", true, "bar", false, "foobar", false);
    private static final Set<String> missingGR = Set.of("bar", "foobar");
    private static final Map<String, Boolean> missingAvailableByGR = Map.of("bar", true, "foobar", true);

    @Mock
    private CatalogService catalogService;
    @Mock
    private L1sDatastripService datastripService;
    @Mock
    private L1sPreparationProperties l1sPreparationProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L1sDatastripManagementService datastripManagementService;

    private L1sDatastrip waitingDatastrip, readyDatastrip, notReadyDatastrip, deletableDatastrip, missingAuxDatastrip;
    private List<L1sDatastrip> waitingDatastripList, readyDatastripList, notReadyDatastripList, deletableDatastripList, missingAuxDatastripList;

    @Override
    public void setup() throws Exception {

        waitingDatastrip = new L1sDatastrip().setGrComplete(false);
        waitingDatastrip.setAvailableByGR(availableByGR);
        waitingDatastrip.setName(DATASTRIP_NAME);

        missingAuxDatastrip = new L1sDatastrip();
        Map<String, Boolean> availableByAux = new HashMap<>();
        availableByAux.put(AuxProductType.GIP_ATMIMA.name(), false);
        missingAuxDatastrip.setAvailableByAux(availableByAux);

        readyDatastrip = new L1sDatastrip();
        readyDatastrip.setReady(true);

        notReadyDatastrip = new L1sDatastrip();
        notReadyDatastrip.setAvailableByAux(Map.of(AuxProductType.GIP_LREXTR.name(), true));

        deletableDatastrip = new L1sDatastrip();
        deletableDatastrip.setJobOrderCreated(true);
        deletableDatastrip.setLastModifiedDate(Instant.now().minus(1, ChronoUnit.HOURS));

        waitingDatastripList = List.of(waitingDatastrip);
        readyDatastripList = List.of(readyDatastrip);
        notReadyDatastripList = List.of(notReadyDatastrip);
        deletableDatastripList = List.of(deletableDatastrip);
        missingAuxDatastripList = List.of(missingAuxDatastrip);

        datastripManagementService = new L1sDatastripManagementService(catalogService, datastripService, l1sPreparationProperties, obsService);
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void updateGRComplete() {
        // Given
        when(datastripService.exists(any())).thenReturn(true);
        when(datastripService.read(any())).thenReturn(waitingDatastrip);
        when(datastripService.update(any())).thenReturn(waitingDatastrip);
        when(l1sPreparationProperties.getL0Bucket()).thenReturn("bucket");
        when(obsService.exists(anyString(), eq(missingGR))).thenReturn(missingAvailableByGR);
        // When
        datastripManagementService.updateGRComplete(DATASTRIP_NAME);
        // Then
        assertTrue(waitingDatastrip.isGrComplete());
    }

    @Test
    void create() throws IOException {
        // Given
        when(l1sPreparationProperties.getSharedFolderRoot()).thenReturn("/tmp");
        when(datastripService.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(new L1sDatastrip());
        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(AUX_UID);
            doAnswer(invocation -> {
                FileUtils.copyDirectoryToDirectory(dsPath.toFile(), new File(TMP_DS_PATH));
                return null;
            }).when(obsService).download(anySet());
            // When
            datastripManagementService.create(DATASTRIP_NAME, "A", Instant.now(), "s3://path/to/storage");
            // Then
            verify(datastripService).create(any(), any(), any(), any(), any(), any(), any());
        } finally {
            FileSystemUtils.deleteRecursively(Paths.get("/tmp/" + AUX_UID));
        }
    }

}
