package eu.csgroup.coprs.ps2.ew.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.ew.l0c.service.exec.ExecutionService;
import eu.csgroup.coprs.ps2.ew.l0c.service.output.OutputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.SetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Configuration
public class ProcessorService {

    private final SetupService setupService;
    private final ExecutionService executionService;
    private final OutputService outputService;


    public ProcessorService(SetupService setupService, ExecutionService executionService, OutputService outputService) {
        this.setupService = setupService;
        this.executionService = executionService;
        this.outputService = outputService;
    }

    @Bean
    @Profile("!test")
    public Function<ProcessingMessage, List<Message<ProcessingMessage>>> process() {

        return processingMessage -> {

            final Instant start = Instant.now();
            log.info("Received message: {}", processingMessage);

            Set<ProcessingMessage> outputMessageSet;

            try {
                final L0cExecutionInput l0cExecutionInput = setupService.setup(processingMessage);

                executionService.execute(l0cExecutionInput);

                outputMessageSet = outputService.output(l0cExecutionInput);

            } catch (Exception e) {
                log.error("Failed to complete execution process for message " + processingMessage, e);
                throw e;
            }

            log.info("Completed execution for message: {} in {}", processingMessage, DateUtils.elapsed(start));

            return outputMessageSet.stream().map(outputMessage -> MessageBuilder.withPayload(outputMessage).build()).toList();
        };

    }

}
