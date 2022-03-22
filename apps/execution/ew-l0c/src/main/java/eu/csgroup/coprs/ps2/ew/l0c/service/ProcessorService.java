package eu.csgroup.coprs.ps2.ew.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.ew.l0c.settings.L0cExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Function;

@Slf4j
@Configuration
public class ProcessorService {

    private final L0cExecutionProperties l0cExecutionProperties;

    public ProcessorService(L0cExecutionProperties l0cExecutionProperties) {
        this.l0cExecutionProperties = l0cExecutionProperties;
    }

    @Bean
    @Profile("!test")
    public Function<ProcessingMessage, ProcessingMessage> process() {

        return processingMessage -> {
            log.info("Received message : {}", processingMessage);
            return processingMessage;
        };

    }

}
