package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class L0cEWOutputService extends EWOutputService<L0cExecutionInput> {

    private final L0cEWUploadService uploadService;

    public L0cEWOutputService(L0cEWMessageService messageService, L0cEWCleanupService cleanupService, L0cEWUploadService uploadService) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
    }

    @Override
    public Set<ProcessingMessage> output(L0cExecutionInput executionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> productsByFamily = uploadService.upload(executionInput);

        final Set<ProcessingMessage> messages = messageService.build(executionInput, productsByFamily);

        cleanupService.clean(executionInput);

        log.info("Finished post execution tasks");

        return messages;
    }

}
