package eu.csgroup.coprs.ps2.ew.l1s.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1s.config.L1sExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l1s.service.setup.L1sEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class L1sEWOutputService extends EWOutputService<L1ExecutionInput> {

    private final L1sExecutionProperties executionProperties;

    protected L1sEWOutputService(L1sEWMessageService messageService, L1sEWCleanupService cleanupService, L1sExecutionProperties executionProperties) {
        super(messageService, cleanupService);
        this.executionProperties = executionProperties;
    }

    @Override
    public Set<ProcessingMessage> output(L1ExecutionInput executionInput) {

        log.info("Starting post execution tasks");

        final Set<ProcessingMessage> messages = messageService.build(executionInput, null);

        if (executionProperties.isCleanup()) {
            cleanupService.clean(executionInput);
        }

        log.info("Finished post execution tasks");

        return messages;
    }

}
