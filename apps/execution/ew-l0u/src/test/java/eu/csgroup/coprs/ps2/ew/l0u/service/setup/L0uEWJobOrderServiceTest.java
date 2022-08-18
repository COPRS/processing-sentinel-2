package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

class L0uEWJobOrderServiceTest extends AbstractTest {

    private L0uEWJobOrderService l0uEWJobOrderService;

    @Override
    public void setup() throws Exception {
        l0uEWJobOrderService = new L0uEWJobOrderService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void saveJobOrders() {
        // Given
        try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
            // When
            l0uEWJobOrderService.saveJobOrders(new L0uExecutionInput().setJobOrders(Map.of("foo", "bar")));
            // Then
            filesMockedStatic.verify(() -> Files.writeString(any(), any()));
        }
    }

}
