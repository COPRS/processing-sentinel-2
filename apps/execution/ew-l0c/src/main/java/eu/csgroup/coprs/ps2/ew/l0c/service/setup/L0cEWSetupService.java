package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L0cEWSetupService implements EWSetupService<L0cExecutionInput> {

    private final L0cEWCleanupService cleanupService;
    private final L0cEWJobOrderService jobOrderService;
    private final L0cEWDownloadService downloadService;

    public L0cEWSetupService(L0cEWCleanupService cleanupService, L0cEWJobOrderService jobOrderService, L0cEWDownloadService downloadService) {
        this.cleanupService = cleanupService;
        this.jobOrderService = jobOrderService;
        this.downloadService = downloadService;
    }

    @Override
    public void setup(L0cExecutionInput l0cExecutionInput) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare();
        jobOrderService.saveJobOrders(l0cExecutionInput);
        downloadService.download(l0cExecutionInput.getFiles());

        log.info("Finished setup.");
    }

}
