package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class L0cEWSetupService implements EWSetupService<L0cExecutionInput> {

    private final L0cEWCleanupService cleanupService;
    private final L0cEWDownloadService downloadService;
    private final SharedProperties sharedProperties;

    public L0cEWSetupService(L0cEWCleanupService cleanupService, L0cEWDownloadService downloadService, SharedProperties sharedProperties) {
        this.cleanupService = cleanupService;
        this.downloadService = downloadService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void setup(L0cExecutionInput l0cExecutionInput, UUID parentUid) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(sharedProperties.getSharedFolderRoot());
        downloadService.download(l0cExecutionInput.getFiles(), parentUid);

        log.info("Finished setup.");
    }

}
