package eu.csgroup.coprs.ps2.ew.l2tl.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

class L2tlEWSetupServiceTest extends AbstractTest {

    @Mock
    private L2tlEWCleanupService cleanupService;
    @Mock
    private L2tlEWDownloadService downloadService;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L2tlEWSetupService setupService;

    @Override
    public void setup() throws Exception {
        setupService = new L2tlEWSetupService(cleanupService, downloadService, sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() {
        // When
        setupService.setup(new L2ExecutionInput(), UUID.randomUUID());
        // Then
        Mockito.verify(cleanupService).cleanAndPrepare(any());
        Mockito.verify(downloadService).download(any(), any());
    }

}
