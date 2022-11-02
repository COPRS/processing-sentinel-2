package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;

import java.util.Set;

public abstract class EWOutputService<T extends ExecutionInput> {

    protected final EWMessageService<T> messageService;
    protected final EWCleanupService<T> cleanupService;

    protected EWOutputService(EWMessageService<T> messageService, EWCleanupService<T> cleanupService) {
        this.messageService = messageService;
        this.cleanupService = cleanupService;
    }

    public abstract Set<ProcessingMessage> output(T executionInput);

}
