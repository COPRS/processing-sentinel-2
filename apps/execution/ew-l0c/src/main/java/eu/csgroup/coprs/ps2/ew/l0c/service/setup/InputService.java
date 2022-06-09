package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.ew.l0c.service.exec.ExecutionService;
import eu.csgroup.coprs.ps2.ew.l0c.settings.EWL0cTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service
public class InputService {

    private final ObjectMapper objectMapper;

    public InputService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public L0cExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final Map<String, Object> additionalFields = processingMessage.getAdditionalFields();

        if (!additionalFields.containsKey(MessageParameters.EXECUTION_INPUT_FIELD) || additionalFields.get(MessageParameters.EXECUTION_INPUT_FIELD) == null) {
            throw new InvalidMessageException("Missing execution input field");
        }

        final L0cExecutionInput l0cExecutionInput = objectMapper.convertValue(additionalFields.get(MessageParameters.EXECUTION_INPUT_FIELD), L0cExecutionInput.class);

        final long taskCount = ExecutionService.getApplicableTasks(Arrays.asList(EWL0cTask.values()), l0cExecutionInput).size();

        if (l0cExecutionInput.getJobOrders().size() != taskCount) {
            throw new InvalidMessageException("Invalid Task count");
        }

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return l0cExecutionInput;
    }

}
