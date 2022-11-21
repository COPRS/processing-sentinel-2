package eu.csgroup.coprs.ps2.core.ew.service.l1;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class L1EWOutputService extends EWOutputService<L1ExecutionInput> {

    protected final SharedProperties sharedProperties;

    protected L1EWOutputService(EWMessageService<L1ExecutionInput> messageService, EWCleanupService<L1ExecutionInput> cleanupService, SharedProperties sharedProperties) {
        super(messageService, cleanupService);
        this.sharedProperties = sharedProperties;
    }

    @Override
    public Set<ProcessingMessage> output(L1ExecutionInput executionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> productsByFamily = upload(executionInput);

        final Set<ProcessingMessage> messages = messageService.build(executionInput, productsByFamily);

        cleanupService.clean(executionInput);

        log.info("Finished post execution tasks");

        return messages;
    }

    protected abstract Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput);

}
