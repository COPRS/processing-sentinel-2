package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class L0uEWSetupServiceTest extends AbstractTest {

    @Mock
    private L0uEWCleanupService cleanupService;
    @Mock
    private L0uEWJobOrderService jobOrderService;
    @Mock
    private L0uEWDownloadService downloadService;

    @InjectMocks
    private L0uEWSetupService l0uEWSetupService;

    @Override
    public void setup() throws Exception {
        l0uEWSetupService = new L0uEWSetupService(cleanupService, jobOrderService, downloadService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() {
        // When
        l0uEWSetupService.setup(new L0uExecutionInput());
        // Then
        verify(cleanupService).cleanAndPrepare();
        verify(jobOrderService).saveJobOrders(any());
        verify(downloadService).download(any());
    }

}
