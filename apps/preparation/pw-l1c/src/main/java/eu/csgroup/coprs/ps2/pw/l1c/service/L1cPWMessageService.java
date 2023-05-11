package eu.csgroup.coprs.ps2.pw.l1c.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.exception.ProcessingException;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1cPWMessageService {

    private final ObjectMapper objectMapper;

    public L1cPWMessageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Set<ProcessingMessage> build(L1ExecutionInput executionInput, Set<String> tileSet) {

        log.info("Building output messages");

        final Set<ProcessingMessage> messages = tileSet.stream()
                .map(tile -> {
                    final L1ExecutionInput tileInput;
                    try {
                        tileInput = objectMapper.readValue(objectMapper.writeValueAsString(executionInput), L1ExecutionInput.class);
                    } catch (JsonProcessingException e) {
                        throw new ProcessingException("Something went horribly wrong");
                    }
                    tileInput.setTile(tile);
                    final ProcessingMessage tileMessage = ProcessingMessageUtils.create().setAllowedActions(getAllowedActions());
                    tileMessage.setSatelliteId(executionInput.getSatellite());
                    tileMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, tileInput);
                    return tileMessage;
                })
                .collect(Collectors.toSet());

        final ProcessingMessage dsMessage = ProcessingMessageUtils.create().setAllowedActions(getAllowedActions());
        dsMessage.setSatelliteId(executionInput.getSatellite());
        dsMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);

        messages.add(dsMessage);

        log.info("Finished building output messages: found {} messages total", messages.size());

        return messages;
    }

    private EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

}
