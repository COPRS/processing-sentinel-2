package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class L0uEWSetupService implements EWSetupService<L0uExecutionInput> {

    private final L0uEWCleanupService cleanupService;
    private final L0uEWJobOrderService jobOrderService;
    private final L0uEWDownloadService downloadService;
    private final L0uExecutionProperties executionProperties;

    public L0uEWSetupService(L0uEWCleanupService cleanupService, L0uEWJobOrderService jobOrderService, L0uEWDownloadService downloadService,
            L0uExecutionProperties executionProperties
    ) {
        this.cleanupService = cleanupService;
        this.jobOrderService = jobOrderService;
        this.downloadService = downloadService;
        this.executionProperties = executionProperties;
    }

    @Override
    public void setup(L0uExecutionInput l0uExecutionInput) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(executionProperties.getOutputFolderRoot());
        jobOrderService.saveJobOrders(l0uExecutionInput);
        downloadService.download(l0uExecutionInput.getFiles());

        log.info("Finished setup.");
    }

}
