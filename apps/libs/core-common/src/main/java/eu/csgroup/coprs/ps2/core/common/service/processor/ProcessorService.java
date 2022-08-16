package eu.csgroup.coprs.ps2.core.common.service.processor;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public abstract class ProcessorService {

    @Bean
    @Profile("!test")
    public Function<ProcessingMessage, List<Message<ProcessingMessage>>> process() {

        return processingMessage -> {

            final Instant start = Instant.now();
            log.info("Received message : {}", processingMessage);

            Set<ProcessingMessage> outputMessageSet;

            try {

                outputMessageSet = processMessage(processingMessage);

            } catch (Exception e) {
                log.error("Failed to process message " + processingMessage, e);
                throw e;
            }

            log.info("Processed message: {} in {}", processingMessage, DateUtils.elapsed(start));

            return outputMessageSet.stream().map(outputMessage -> MessageBuilder.withPayload(outputMessage).build()).toList();

        };

    }

    protected abstract Set<ProcessingMessage> processMessage(ProcessingMessage processingMessage);

}
