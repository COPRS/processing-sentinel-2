package eu.csgroup.coprs.ps2.ew.l1sa.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class L1saEWSetupService implements EWSetupService<L1ExecutionInput> {

    private final L1saEWCleanupService cleanupService;
    private final L1saEWDownloadService downloadService;
    private final SharedProperties sharedProperties;

    public L1saEWSetupService(L1saEWCleanupService cleanupService, L1saEWDownloadService downloadService, SharedProperties sharedProperties) {
        this.cleanupService = cleanupService;
        this.downloadService = downloadService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void setup(L1ExecutionInput executionInput, UUID parentUid) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(sharedProperties.getSharedFolderRoot());
        downloadService.download(executionInput.getFiles(), parentUid);

        log.info("Finished setup.");
    }

}
