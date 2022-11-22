package eu.csgroup.coprs.ps2.ew.l1sb.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1sbEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1sbEWCleanupService cleanupService;
    private final SharedProperties sharedProperties;

    public L1sbEWSetupService(L1sbEWCleanupService cleanupService, SharedProperties sharedProperties) {
        this.cleanupService = cleanupService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void setup(L1ExecutionInput executionInput) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(sharedProperties.getSharedFolderRoot());

        log.info("Finished setup.");
    }

}