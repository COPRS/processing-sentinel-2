package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.ew.l0c.config.L0cExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.CleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class OutputService {

    private final UploadService uploadService;
    private final MessageService messageService;
    private final CleanupService cleanupService;
    private final L0cExecutionProperties l0cExecutionProperties;

    public OutputService(UploadService uploadService, MessageService messageService, CleanupService cleanupService, L0cExecutionProperties l0cExecutionProperties) {
        this.uploadService = uploadService;
        this.messageService = messageService;
        this.cleanupService = cleanupService;
        this.l0cExecutionProperties = l0cExecutionProperties;
    }

    public Set<ProcessingMessage> output(L0cExecutionInput l0cExecutionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> productsByFamily = uploadService.upload();

        final Set<ProcessingMessage> messages = messageService.build(l0cExecutionInput, productsByFamily);

        if (l0cExecutionProperties.isCleanup()) {
            cleanupService.clean(l0cExecutionInput);
        }

        log.info("Finished post execution tasks");

        return messages;
    }

}
