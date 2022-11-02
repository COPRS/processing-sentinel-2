package eu.csgroup.coprs.ps2.ew.l1s.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1sEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1sEWCleanupService cleanupService;
    private final L1sEWDownloadService downloadService;

    public L1sEWSetupService(L1sEWCleanupService cleanupService, L1sEWDownloadService downloadService) {
        this.cleanupService = cleanupService;
        this.downloadService = downloadService;
    }

    @Override
    public void setup(L1ExecutionInput executionInput) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare();
        downloadService.download(executionInput.getFiles());

        log.info("Finished setup.");
    }

}
