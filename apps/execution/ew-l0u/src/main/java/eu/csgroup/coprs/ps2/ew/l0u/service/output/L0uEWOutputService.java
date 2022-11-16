package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class L0uEWOutputService extends EWOutputService<L0uExecutionInput> {

    private final L0uEWUploadService uploadService;
    private final L0uEWCopyService copyService;


    public L0uEWOutputService(L0uEWMessageService messageService, L0uEWCleanupService cleanupService, L0uEWUploadService uploadService, L0uEWCopyService copyService) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
        this.copyService = copyService;
    }

    @Override
    public Set<ProcessingMessage> output(L0uExecutionInput executionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> auxFileInfosByFamily = uploadService.upload(executionInput);

        final String outputFolder = copyService.copy();

        final Set<ProcessingMessage> messages = messageService.build(executionInput, auxFileInfosByFamily, outputFolder);

        cleanupService.clean(executionInput);

        log.info("Finished post execution tasks");

        return messages;
    }

}
