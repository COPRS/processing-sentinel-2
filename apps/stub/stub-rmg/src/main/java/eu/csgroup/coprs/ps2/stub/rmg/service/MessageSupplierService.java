package eu.csgroup.coprs.ps2.stub.rmg.service;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.stub.rmg.config.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class MessageSupplierService {

    private final Parameters parameters;

    private PodamFactory podamFactory;

    public MessageSupplierService(Parameters parameters) {
        this.parameters = parameters;
        this.podamFactory = new PodamFactoryImpl();
    }

    @Bean
    @Profile("!test")
    public Supplier<ProcessingMessage> supply() {

        return () -> {

            ProcessingMessage processingMessage = podamFactory.manufacturePojo(ProcessingMessage.class);

            String uid = UUID.randomUUID().toString();
            processingMessage.setUid(uid);
            processingMessage.setCreationDate(DateUtils.toDate(Instant.now()));
            processingMessage.setMissionId("S2");

            log.info("Sending message with UID: {}", uid);

            return processingMessage;
        };
    }

}
