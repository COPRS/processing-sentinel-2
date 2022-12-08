package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class L0uEWSetupServiceTest extends AbstractTest {

    @Mock
    private L0uEWCleanupService cleanupService;
    @Mock
    private L0uEWJobOrderService jobOrderService;
    @Mock
    private L0uEWDownloadService downloadService;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L0uEWSetupService l0uEWSetupService;

    @Override
    public void setup() throws Exception {
        l0uEWSetupService = new L0uEWSetupService(cleanupService, jobOrderService, downloadService, sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() {
        // When
        when(sharedProperties.getSharedFolderRoot()).thenReturn("foo");
        // When
        l0uEWSetupService.setup(new L0uExecutionInput(), null);
        // Then
        verify(cleanupService).cleanAndPrepare("foo");
        verify(jobOrderService).saveJobOrders(any());
        verify(downloadService).download(any(), any());
    }

}
