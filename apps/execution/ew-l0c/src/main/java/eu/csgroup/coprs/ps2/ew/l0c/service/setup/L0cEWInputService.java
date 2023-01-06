package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWInputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class L0cEWInputService implements EWInputService<L0cExecutionInput> {

    @Override
    public L0cExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final L0cExecutionInput l0cExecutionInput = ProcessingMessageUtils.getAdditionalField(
                processingMessage,
                MessageParameters.EXECUTION_INPUT_FIELD,
                L0cExecutionInput.class
        );

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return l0cExecutionInput;
    }

    @Override
    public Set<String> getTaskInputs(L0cExecutionInput executionInput) {
        return Collections.singleton(executionInput.getDatastrip());
    }

}
