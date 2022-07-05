package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class L0uEWOutputService implements EWOutputService<L0uExecutionInput> {

    private final L0uEWUploadService uploadService;
    private final L0uEWCopyService copyService;
    private final L0uEWMessageService messageService;
    private final L0uEWCleanupService cleanupService;
    private final L0uExecutionProperties l0uExecutionProperties;

    public L0uEWOutputService(L0uEWUploadService uploadService, L0uEWCopyService copyService, L0uEWMessageService messageService, L0uEWCleanupService cleanupService,
            L0uExecutionProperties l0uExecutionProperties
    ) {
        this.uploadService = uploadService;
        this.copyService = copyService;
        this.messageService = messageService;
        this.cleanupService = cleanupService;
        this.l0uExecutionProperties = l0uExecutionProperties;
    }

    @Override
    public Set<ProcessingMessage> output(L0uExecutionInput l0uExecutionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> auxFileInfosByFamily = uploadService.upload();

        final String outputFolder = copyService.copy();

        final Set<ProcessingMessage> messages = messageService.build(l0uExecutionInput, auxFileInfosByFamily, outputFolder);

        if (l0uExecutionProperties.isCleanup()) {
            cleanupService.clean();
        }

        log.info("Finished post execution tasks");

        return messages;
    }

}
