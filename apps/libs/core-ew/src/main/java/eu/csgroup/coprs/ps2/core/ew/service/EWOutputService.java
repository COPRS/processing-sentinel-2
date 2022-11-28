package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class EWOutputService<T extends ExecutionInput> {

    protected final EWMessageService<T> messageService;
    protected final EWCleanupService<T> cleanupService;

    protected EWOutputService(EWMessageService<T> messageService, EWCleanupService<T> cleanupService) {
        this.messageService = messageService;
        this.cleanupService = cleanupService;
    }

    public Set<ProcessingMessage> output(T executionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = upload(executionInput);

        final String outputFolder = copy();

        final Set<ProcessingMessage> messages = messageService.build(executionInput, fileInfoByFamily, outputFolder);

        cleanupService.clean(executionInput);

        log.info("Finished post execution tasks");

        return messages;
    }

    protected abstract Map<ProductFamily, Set<FileInfo>> upload(T executionInput);

    protected String copy() {
        // By default, noting to do
        return null;
    }

}
