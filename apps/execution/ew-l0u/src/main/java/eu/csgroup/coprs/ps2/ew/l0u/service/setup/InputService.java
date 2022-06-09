package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component
public class InputService {

    private final ObjectMapper objectMapper;

    public InputService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public L0uExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final Map<String, Object> additionalFields = processingMessage.getAdditionalFields();

        if (!additionalFields.containsKey(MessageParameters.EXECUTION_INPUT_FIELD) || additionalFields.get(MessageParameters.EXECUTION_INPUT_FIELD) == null) {
            throw new InvalidMessageException("Missing execution input field");
        }

        final L0uExecutionInput l0uExecutionInput = objectMapper.convertValue(additionalFields.get(MessageParameters.EXECUTION_INPUT_FIELD), L0uExecutionInput.class);

        if (l0uExecutionInput.getJobOrders().size() != 1) {
            throw new InvalidMessageException("Invalid Job Order count");
        }

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return l0uExecutionInput;
    }

}
