package eu.csgroup.coprs.ps2.ew.l1sb.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import eu.csgroup.coprs.ps2.ew.l1sb.config.L1sbExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1sbEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1sbEWCleanupService cleanupService;
    private final L1sbExecutionProperties executionProperties;

    public L1sbEWSetupService(L1sbEWCleanupService cleanupService, L1sbExecutionProperties executionProperties) {
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
