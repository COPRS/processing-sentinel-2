package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public abstract class PWMessageService<T extends ExecutionInput> { // NOSONAR

    public Set<ProcessingMessage> build(List<T> executionInputList) {

        log.info("Creating output messages for all executions ({})", executionInputList.size());

        Set<ProcessingMessage> messages = new HashSet<>();

        executionInputList.forEach(executionInput -> {

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
