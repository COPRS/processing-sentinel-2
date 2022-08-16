package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l0c.config.L0cExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class L0cEWOutputService implements EWOutputService<L0cExecutionInput> {

    private final L0cEWUploadService uploadService;
    private final L0cEWMessageService messageService;
    private final L0cEWCleanupService cleanupService;
    private final L0cExecutionProperties l0cExecutionProperties;

    public L0cEWOutputService(L0cEWUploadService uploadService, L0cEWMessageService messageService, L0cEWCleanupService cleanupService,
            L0cExecutionProperties l0cExecutionProperties
    ) {
        this.uploadService = uploadService;
        this.messageService = messageService;
        this.cleanupService = cleanupService;
        this.l0cExecutionProperties = l0cExecutionProperties;
    }

    @Override
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
