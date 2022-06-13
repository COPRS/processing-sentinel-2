package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetupService {

    private final CleanupService cleanupService;
    private final InputService inputService;
    private final JobOrderService jobOrderService;
    private final DownloadService downloadService;

    public SetupService(CleanupService cleanupService, InputService inputService, JobOrderService jobOrderService, DownloadService downloadService) {
        this.cleanupService = cleanupService;
        this.inputService = inputService;
        this.jobOrderService = jobOrderService;
        this.downloadService = downloadService;
    }


    public L0uExecutionInput setup(ProcessingMessage processingMessage) {

        // TODO Retry / Restart / ... ???

        log.info("Starting setup for message: {}", processingMessage);

        cleanupService.clean();
        L0uExecutionInput l0uExecutionInput = inputService.extract(processingMessage);
        jobOrderService.saveJobOrders(l0uExecutionInput);
        downloadService.download(l0uExecutionInput.getFiles());

        log.info("Finished setup for message: {}", processingMessage);

        return l0uExecutionInput;
    }

}
