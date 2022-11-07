package eu.csgroup.coprs.ps2.ew.l1ab.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1ab.config.L1abExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class L1abEWOutputService extends EWOutputService<L1ExecutionInput> {

    private final L1abEWUploadService uploadService;
    private final L1abExecutionProperties executionProperties;

    public L1abEWOutputService(L1abEWMessageService messageService, L1abEWCleanupService cleanupService, L1abEWUploadService uploadService,
            L1abExecutionProperties executionProperties
    ) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
        this.executionProperties = executionProperties;
    }

    @Override
    public Set<ProcessingMessage> output(L1ExecutionInput executionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> productsByFamily = uploadService.upload(executionInput);

        final Set<ProcessingMessage> messages = messageService.build(executionInput, productsByFamily);

        if (executionProperties.isCleanup()) {
            cleanupService.clean(executionInput);
        }

        log.info("Finished post execution tasks");

        return messages;
    }

}
