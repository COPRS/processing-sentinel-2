package eu.csgroup.coprs.ps2.ew.l1c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class L1cEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1cEWCleanupService cleanupService;
    private final SharedProperties sharedProperties;

    public L1cEWSetupService(L1cEWCleanupService cleanupService, SharedProperties sharedProperties) {
        this.cleanupService = cleanupService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void setup(L1ExecutionInput executionInput, UUID parentUid) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(sharedProperties.getSharedFolderRoot());

        log.info("Finished setup.");
    }

}
