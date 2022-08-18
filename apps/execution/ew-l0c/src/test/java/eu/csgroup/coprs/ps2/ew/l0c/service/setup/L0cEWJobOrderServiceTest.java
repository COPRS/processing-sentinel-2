package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

class L0cEWJobOrderServiceTest extends AbstractTest {

    private L0cEWJobOrderService l0cEWJobOrderService;

    @Override
    public void setup() throws Exception {
        l0cEWJobOrderService = new L0cEWJobOrderService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void saveJobOrders() {

        // Given
        final L0cExecutionInput executionInput = new L0cExecutionInput()
                .setJobOrders(Map.of("foo", Map.of("bar", "bar")));

        try (
                MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class);
                MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)
        ) {

            // When
            l0cEWJobOrderService.saveJobOrders(executionInput);

            // Then
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.createFolders(any()));
        }
    }

}
