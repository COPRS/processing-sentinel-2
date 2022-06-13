package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class OutputService {

    private final UploadService uploadService;
    private final CopyService copyService;
    private final MessageService messageService;

    public OutputService(UploadService uploadService, CopyService copyService, MessageService messageService) {
        this.uploadService = uploadService;
        this.copyService = copyService;
        this.messageService = messageService;
    }

    public Set<ProcessingMessage> output(L0uExecutionInput l0uExecutionInput) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> auxFileInfosByFamily = uploadService.upload();

        final String outputFolder = copyService.copy();

        final Set<ProcessingMessage> messages = messageService.build(outputFolder, l0uExecutionInput, auxFileInfosByFamily);

        log.info("Finished post execution tasks");

        return messages;
    }

}
