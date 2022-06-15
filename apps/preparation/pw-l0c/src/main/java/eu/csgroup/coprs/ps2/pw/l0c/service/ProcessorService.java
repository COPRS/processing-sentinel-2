package eu.csgroup.coprs.ps2.pw.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.pw.l0c.service.output.MessageService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.DatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.ExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0c.service.setup.InputService;
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
    private final DatastripManagementService datastripManagementService;
    private final ExecutionInputService executionInputService;
    private final MessageService messageService;

    public ProcessorService(InputService inputService, DatastripManagementService datastripManagementService, ExecutionInputService executionInputService,
            MessageService messageService
    ) {
        this.inputService = inputService;
        this.datastripManagementService = datastripManagementService;
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

                final L0cPreparationInput l0cPreparationInput = inputService.extractInput(processingMessage);

                inputService.getDatastrips(l0cPreparationInput.getInputFolder())
                        .forEach(datastripPath -> datastripManagementService.create(datastripPath, l0cPreparationInput.getSatellite(), l0cPreparationInput.getStation()));

                datastripManagementService.updateAvailableAux();
                datastripManagementService.updateNotReady();

                log.info("Fetching ready datastrips ...");

                final List<Datastrip> readyDatastrips = datastripManagementService.getReady();

                log.info("Found {} ready datastrips", readyDatastrips.size());

                if (!CollectionUtils.isEmpty(readyDatastrips)) {
                    List<L0cExecutionInput> l0uExecutionInputList = executionInputService.create(readyDatastrips);
                    outputMessageSet = messageService.build(l0uExecutionInputList);
                    datastripManagementService.setJobOrderCreated(readyDatastrips);
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
