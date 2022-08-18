package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class L0cEWSetupServiceTest extends AbstractTest {

    @Mock
    private L0cEWCleanupService cleanupService;
    @Mock
    private L0cEWJobOrderService jobOrderService;
    @Mock
    private L0cEWDownloadService downloadService;

    @InjectMocks
    private L0cEWSetupService l0cEWSetupService;

    @Override
    public void setup() throws Exception {
        l0cEWSetupService = new L0cEWSetupService(cleanupService, jobOrderService, downloadService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() {
        // Given
        final L0cExecutionInput executionInput = new L0cExecutionInput();
        // When
        l0cEWSetupService.setup(executionInput);
        // Then
        verify(cleanupService).cleanAndPrepare();
        verify(jobOrderService).saveJobOrders(executionInput);
        verify(downloadService).download(any());
    }

}
