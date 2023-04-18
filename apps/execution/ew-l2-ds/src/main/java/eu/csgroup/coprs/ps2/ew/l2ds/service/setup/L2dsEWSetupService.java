package eu.csgroup.coprs.ps2.ew.l2ds.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class L2dsEWSetupService implements EWSetupService<L2ExecutionInput> {

    private final L2dsEWCleanupService cleanupService;
    private final L2dsEWDownloadService downloadService;
    private final SharedProperties sharedProperties;

    public L2dsEWSetupService(L2dsEWCleanupService cleanupService, L2dsEWDownloadService downloadService, SharedProperties sharedProperties) {
        this.cleanupService = cleanupService;
        this.downloadService = downloadService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void setup(L2ExecutionInput executionInput, UUID parentUid) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(sharedProperties.getSharedFolderRoot());
        downloadService.download(executionInput.getFiles(), parentUid);

        log.info("Finished setup.");
    }

}
