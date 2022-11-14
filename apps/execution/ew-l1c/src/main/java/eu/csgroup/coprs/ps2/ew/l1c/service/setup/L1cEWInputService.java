package eu.csgroup.coprs.ps2.ew.l1c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWInputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1cEWInputService implements EWInputService<L1ExecutionInput> {

    @Override
    public L1ExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final L1ExecutionInput l1ExecutionInput = ProcessingMessageUtils.getAdditionalField(
                processingMessage,
                MessageParameters.EXECUTION_INPUT_FIELD,
                L1ExecutionInput.class
        );

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return l1ExecutionInput;
    }

    @Override
    public Set<String> getTaskInputs(L1ExecutionInput executionInput) {
        return executionInput.getCustomTaskInputs()
                .stream().filter(s -> s.contains("L1C"))
                .collect(Collectors.toSet());
    }

}
