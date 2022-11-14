package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.config.L1ExecutionProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class L1EWOutputService extends EWOutputService<L1ExecutionInput> {

    protected final L1ExecutionProperties executionProperties;

    protected L1EWOutputService(EWMessageService<L1ExecutionInput> messageService, EWCleanupService<L1ExecutionInput> cleanupService, L1ExecutionProperties executionProperties) {
        super(messageService, cleanupService);
        this.executionProperties = executionProperties;
    }

    @Override
    public Set<ProcessingMessage> output(L1ExecutionInput executionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> productsByFamily = upload(executionInput);

        final Set<ProcessingMessage> messages = messageService.build(executionInput, productsByFamily);

        if (executionProperties.isCleanup()) {
            cleanupService.clean(executionInput);
        }

        log.info("Finished post execution tasks");

        return messages;
    }

    protected abstract Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput);

}
