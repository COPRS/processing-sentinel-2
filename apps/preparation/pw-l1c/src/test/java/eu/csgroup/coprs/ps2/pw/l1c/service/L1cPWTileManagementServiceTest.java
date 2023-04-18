package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class L1cPWTileManagementServiceTest extends AbstractTest {

    private L1cPWTileManagementService tileManagementService;

    @Override
    public void setup() throws Exception {
        tileManagementService = new L1cPWTileManagementService();
    }

    @Override
    public void teardown() throws Exception {

    }

    @Test
    void listTiles() {
        // Given
        try (
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<FileContentUtils> fileContentUtilsMockedStatic = Mockito.mockStatic(FileContentUtils.class);
        ) {
            filesMockedStatic.when(() -> Files.exists(any())).thenReturn(true);
            fileContentUtilsMockedStatic.when(() -> FileContentUtils.grepAll(any(), any())).thenReturn(List.of("1", "2", "3", "4"));

            // When
            final Set<String> tiles = tileManagementService.listTiles((L1ExecutionInput) new L1ExecutionInput().setOutputFolder("output"));
            // Then
            assertEquals(4, tiles.size());
        }
    }

}
