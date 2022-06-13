package eu.csgroup.coprs.ps2.pw.l0c.service.setup;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
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

    public L0cPreparationInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting preparation input from message: {}", processingMessage);

        final Map<String, Object> additionalFields = processingMessage.getAdditionalFields();

        if (!additionalFields.containsKey(MessageParameters.PREPARATION_INPUT_FIELD) || additionalFields.get(MessageParameters.PREPARATION_INPUT_FIELD) == null) {
            throw new InvalidMessageException("Missing preparation input field");
        }

        final L0cPreparationInput l0cPreparationInput = objectMapper.convertValue(additionalFields.get(MessageParameters.PREPARATION_INPUT_FIELD), L0cPreparationInput.class);

        if (l0cPreparationInput.getInputFolder().isBlank() || l0cPreparationInput.getSession().isBlank()) {
            throw new InvalidMessageException("Invalid Preparation Data");
        }

        return l0cPreparationInput;
    }

}
