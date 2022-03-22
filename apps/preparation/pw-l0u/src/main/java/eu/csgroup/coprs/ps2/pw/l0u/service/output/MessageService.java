package eu.csgroup.coprs.ps2.pw.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.execution.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class MessageService {

    public Set<ProcessingMessage> build(List<L0uExecutionInput> l0uExecutionInputList) {

        log.info("Creating output messages for all executions ({})", l0uExecutionInputList.size());

        Set<ProcessingMessage> messages = new HashSet<>();

        l0uExecutionInputList.forEach(executionInput -> {

            final ProcessingMessage processingMessage = ProcessingMessageUtils.create()
                    .setSatelliteId(executionInput.getSatellite())
                    .setKeyObjectStorage(MessageParameters.EMPTY)
                    .setStoragePath(MessageParameters.EMPTY)
                    .setProductFamily(ProductFamily.S2_L0_DS);

            processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);

            messages.add(processingMessage);
        });

        log.info("Finished creating output messages");

        return messages;
    }

}
