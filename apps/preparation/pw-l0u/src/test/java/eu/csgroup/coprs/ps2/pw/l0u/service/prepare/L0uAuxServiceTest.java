package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.PreparationParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l0u.config.L0uPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0u.model.AuxValue;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class L0uAuxServiceTest extends AbstractTest {

    private static final Map<AuxProductType, String> AUX_BY_TYPE = Map.of(
            AuxProductType.AUX_UT1UTC, "S2__OPER_AUX_UT1UTC_PDMC_20220407T000000_V20220408T000000_20230407T000000",
            AuxProductType.GIP_DATATI, "S2B_OPER_GIP_DATATI_MPC__20170428T123038_V20170322T000000_21000101T000000_B00",
            AuxProductType.GIP_ATMIMA, "S2B_OPER_GIP_ATMIMA_MPC__20170206T103051_V20170101T000000_21000101T000000_B00",
            AuxProductType.GIP_ATMSAD, "S2B_OPER_GIP_ATMSAD_MPC__20170324T155501_V20170306T000000_21000101T000000_B00"
    );

    private static final String AUX_FOLDER = "aux";
    private static final UUID AUX_UID = UUID.randomUUID();
    private static final Path AUX_PATH = Paths.get("src/test/resources/" + AUX_FOLDER);
    private static final Path TMP_PATH = Paths.get(PreparationParameters.TMP_DOWNLOAD_FOLDER, AUX_UID.toString());


    @Mock
    private CatalogService catalogService;
    @Mock
    private L0uPreparationProperties l0uPreparationProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private L0uAuxService auxService;

    @Override
    public void setup() throws Exception {
        auxService = new L0uAuxService(catalogService, l0uPreparationProperties, obsService);
        FileSystemUtils.copyRecursively(AUX_PATH, TMP_PATH);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getValues() throws Exception {

        // Given
        mockCatalog();
        when(l0uPreparationProperties.getAuxBucket()).thenReturn("bucket");

        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(AUX_UID);

            // When
            final Map<AuxValue, String> values = auxService.getValues(TestHelper.SATELLITE, TestHelper.START_TIME, TestHelper.STOP_TIME);

            // Then
            assertNotNull(values);
            assertEquals(AuxValue.values().length, values.size());
            assertFalse(Files.exists(TMP_PATH));
        }
    }

    private void mockCatalog() {
        AUX_BY_TYPE.forEach((productType, name) ->
                when(catalogService.retrieveLatestAuxData(eq(productType), any(), any(), any()))
                        .thenReturn(Optional.of(((AuxCatalogData) new AuxCatalogData().setKeyObjectStorage(name))))
        );
    }

}
