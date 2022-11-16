package eu.csgroup.coprs.ps2.ew.l1ab.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import eu.csgroup.coprs.ps2.ew.l1ab.config.L1abExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1abEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1abEWCleanupService cleanupService;
    private final L1abExecutionProperties executionProperties;

    public L1abEWSetupService(L1abEWCleanupService cleanupService, L1abExecutionProperties executionProperties) {
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
