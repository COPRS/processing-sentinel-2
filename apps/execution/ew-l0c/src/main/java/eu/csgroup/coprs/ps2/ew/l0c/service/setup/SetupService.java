package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SetupService {

    private final CleanupService cleanupService;
    private final InputService inputService;
    private final EWJobOrderService jobOrderService;
    private final DownloadService downloadService;

    public SetupService(CleanupService cleanupService, InputService inputService, EWJobOrderService jobOrderService, DownloadService downloadService) {
        this.cleanupService = cleanupService;
        this.inputService = inputService;
        this.jobOrderService = jobOrderService;
        this.downloadService = downloadService;
    }

    public L0cExecutionInput setup(ProcessingMessage processingMessage) {

        log.info("Starting setup for message: {}", processingMessage);

        cleanupService.cleanAndPrepare();
        L0cExecutionInput l0cExecutionInput = inputService.extract(processingMessage);
        jobOrderService.saveJobOrders(l0cExecutionInput);
        downloadService.download(l0cExecutionInput.getFiles());

        log.info("Finished setup for message: {}", processingMessage);

        return l0cExecutionInput;
    }

}
