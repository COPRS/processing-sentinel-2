package eu.csgroup.coprs.ps2.ew.l1c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import eu.csgroup.coprs.ps2.ew.l1c.config.L1cExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1cEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1cEWCleanupService cleanupService;
    private final L1cExecutionProperties executionProperties;

    public L1cEWSetupService(L1cEWCleanupService cleanupService, L1cExecutionProperties executionProperties) {
        this.cleanupService = cleanupService;
        this.executionProperties = executionProperties;
    }

    @Override
    public void setup(L1ExecutionInput executionInput) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(executionProperties.getSharedFolderRoot());

        log.info("Finished setup.");
    }

}
