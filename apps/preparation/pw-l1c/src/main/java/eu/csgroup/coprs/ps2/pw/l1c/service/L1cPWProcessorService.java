package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.service.processor.ProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Configuration
public class L1cPWProcessorService extends ProcessorService {

    @Override
    protected Set<ProcessingMessage> processMessage(ProcessingMessage processingMessage) {
        return Collections.emptySet();
    }

}
