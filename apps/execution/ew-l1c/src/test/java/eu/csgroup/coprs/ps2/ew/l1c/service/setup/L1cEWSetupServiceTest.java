package eu.csgroup.coprs.ps2.ew.l1c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

class L1cEWSetupServiceTest extends AbstractTest {

    @Mock
    private L1cEWCleanupService cleanupService;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L1cEWSetupService setupService;

    @Override
    public void setup() throws Exception {
        setupService = new L1cEWSetupService(cleanupService, sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void testSetup() {
        // When
        setupService.setup(new L1ExecutionInput(), UUID.randomUUID());
        // Then
        Mockito.verify(cleanupService).cleanAndPrepare(any());
    }

}
