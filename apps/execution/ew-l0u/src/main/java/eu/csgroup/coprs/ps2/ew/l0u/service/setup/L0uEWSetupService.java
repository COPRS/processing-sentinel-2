package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class L0uEWSetupService implements EWSetupService<L0uExecutionInput> {

    private final L0uEWCleanupService cleanupService;
    private final L0uEWJobOrderService jobOrderService;
    private final L0uEWDownloadService downloadService;
    private final SharedProperties sharedProperties;

    public L0uEWSetupService(L0uEWCleanupService cleanupService, L0uEWJobOrderService jobOrderService, L0uEWDownloadService downloadService,
            SharedProperties sharedProperties
    ) {
        this.cleanupService = cleanupService;
        this.jobOrderService = jobOrderService;
        this.downloadService = downloadService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void setup(L0uExecutionInput l0uExecutionInput, UUID parentUid) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(sharedProperties.getSharedFolderRoot());
        jobOrderService.saveJobOrders(l0uExecutionInput);
        downloadService.download(l0uExecutionInput.getFiles(), parentUid);

        log.info("Finished setup.");
    }

}
