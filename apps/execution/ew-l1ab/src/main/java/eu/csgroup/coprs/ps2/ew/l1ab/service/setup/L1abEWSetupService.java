package eu.csgroup.coprs.ps2.ew.l1ab.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1abEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1abEWCleanupService cleanupService;

    public L1abEWSetupService(L1abEWCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @Override
    public void setup(L1ExecutionInput executionInput) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare();

        log.info("Finished setup.");
    }

}
