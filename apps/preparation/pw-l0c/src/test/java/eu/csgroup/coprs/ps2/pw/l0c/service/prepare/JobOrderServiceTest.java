package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.exception.InvalidInputException;
import eu.csgroup.coprs.ps2.core.common.settings.PreparationParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class JobOrderServiceTest extends AbstractTest {

    private static final String AUX_FOLDER = "aux";
    private static final UUID AUX_UID = UUID.randomUUID();
    private static final Path AUX_PATH = Paths.get("src/test/resources/" + AUX_FOLDER);
    private static final Path TMP_PATH = Paths.get(PreparationParameters.TMP_DOWNLOAD_FOLDER, AUX_UID.toString());

    @Mock
    private L0cPreparationProperties l0cPreparationProperties;
    @Mock
    private ObsService obsService;

    @InjectMocks
    private JobOrderService jobOrderService;

    @Override
    public void setup() throws Exception {
        jobOrderService = new JobOrderService(l0cPreparationProperties, obsService);
        FileSystemUtils.copyRecursively(AUX_PATH, TMP_PATH);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() throws IllegalAccessException {
        // Given
        when(l0cPreparationProperties.getDemFolderRoot()).thenReturn(TestHelper.DEM_FULL_PATH);
        // When
        jobOrderService.setup();
        // Then
        final String demFolder = (String) FieldUtils.readField(jobOrderService, "demFolder", true);
        assertEquals(TestHelper.DEM_NAME, demFolder);
    }

    @Test
    void testSetup_invalid() {
        // Given
        when(l0cPreparationProperties.getDemFolderRoot()).thenReturn(TestHelper.DEM_BAD_FULL_PATH);
        // When Then
        assertThrows(InvalidInputException.class, () -> jobOrderService.setup());
    }

    @Test
    void testSetup_fails() {
        // Given
        when(l0cPreparationProperties.getDemFolderRoot()).thenReturn("foo");
        // When Then
        assertThrows(InvalidInputException.class, () -> jobOrderService.setup());
    }

    @Test
    void create() {
        // Given
        ReflectionTestUtils.setField(jobOrderService, "demFolder", TestHelper.DEM_NAME);

        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(AUX_UID);

            // When
            final Map<String, Map<String, String>> jobOrdersByTask = jobOrderService.create(TestHelper.DATASTRIP, TestHelper.auxFilesByType);

            // Then
            assertEquals(9, jobOrdersByTask.size());

        }

    }

}
