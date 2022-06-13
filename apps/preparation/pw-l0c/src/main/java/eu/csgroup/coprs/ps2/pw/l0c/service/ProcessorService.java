package eu.csgroup.coprs.ps2.pw.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.pw.l0c.service.setup.InputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Configuration
public class ProcessorService {

    private final InputService inputService;


    public ProcessorService(InputService inputService) {
        this.inputService = inputService;
    }

    @Bean
    @Profile("!test")
    public Function<ProcessingMessage, List<Message<ProcessingMessage>>> process() {

        return processingMessage -> {

            log.info("Received message : {}", processingMessage);

            Set<ProcessingMessage> outputMessageSet = new HashSet<>();

            final L0cPreparationInput l0cPreparationInput = inputService.extract(processingMessage);


            // TODO map datastrip by DT
            // TODO find a way so tore DS/DT info in DB



            log.info("Completed preparation for message: {}", processingMessage);

            return outputMessageSet.stream().map(outputMessage -> MessageBuilder.withPayload(outputMessage).build()).toList();
        };

    }

}
