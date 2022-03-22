package eu.csgroup.coprs.ps2.pw.l0u.service;

import eu.csgroup.coprs.ps2.core.common.model.execution.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.preparation.FileType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import eu.csgroup.coprs.ps2.core.common.utils.SessionUtils;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.service.output.MessageService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.ExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionManagementService;
import eu.csgroup.coprs.ps2.pw.l0u.service.setup.InputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Configuration
public class ProcessorService {

    private final InputService inputService;
    private final SessionManagementService sessionManagementService;
    private final ExecutionInputService executionInputService;
    private final MessageService messageService;

    public ProcessorService(InputService inputService, SessionManagementService sessionManagementService, ExecutionInputService executionInputService,
            MessageService messageService
    ) {
        this.inputService = inputService;
        this.sessionManagementService = sessionManagementService;
        this.executionInputService = executionInputService;
        this.messageService = messageService;
    }


    @Bean
    @Profile("!test")
    public Function<ProcessingMessage, List<Message<ProcessingMessage>>> process() {

        return processingMessage -> {

            log.info("Received message : {}", processingMessage);

            Set<ProcessingMessage> outputMessageSet = new HashSet<>();

            try {

                FileType fileType = inputService.extract(processingMessage);
                String fileName = ObsUtils.keyToName(processingMessage.getKeyObjectStorage());

                log.info("Received message for {} file: {}", fileType.name(), fileName);

                if (fileType != FileType.UNKNOWN) {

                    if (fileType == FileType.DSIB) {
                        sessionManagementService.create(SessionUtils.sessionFromFilename(fileName));
                    } else if (fileType == FileType.DSDB) {
                        sessionManagementService.updateRawComplete(SessionUtils.sessionFromFilename(fileName));
                    }

                    sessionManagementService.updateAvailableAux();
                    sessionManagementService.updateNotReady();

                    log.info("Fetching ready sessions ...");

                    final List<Session> readySessions = sessionManagementService.getReady();

                    log.info("Found {} ready sessions", readySessions.size());

                    if (!CollectionUtils.isEmpty(readySessions)) {
                        List<L0uExecutionInput> l0uExecutionInputList = executionInputService.create(readySessions);
                        outputMessageSet = messageService.build(l0uExecutionInputList);
                        sessionManagementService.setJobOrderCreated(readySessions);
                    }
                }

            } catch (Exception e) {
                log.error("Failed to complete preparation for message " + processingMessage, e);
                throw e;
            }

            log.info("Completed preparation for message: {}", processingMessage);

            return outputMessageSet.stream().map(outputMessage -> MessageBuilder.withPayload(outputMessage).build()).toList();
        };
    }

}
