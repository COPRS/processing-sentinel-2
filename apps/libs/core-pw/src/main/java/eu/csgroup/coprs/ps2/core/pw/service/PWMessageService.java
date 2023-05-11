package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public abstract class PWMessageService<T extends ExecutionInput> { // NOSONAR

    protected abstract EventAction[] getAllowedActions();

    protected abstract Map<String, Object> getResubmitInfos(ProcessingMessage processingMessage);

    public Set<ProcessingMessage> build(List<T> executionInputList, ProcessingMessage inputProcessingMessage) {

        log.info("Creating output messages for all executions ({})", executionInputList.size());

        Set<ProcessingMessage> messages = new HashSet<>();

        executionInputList.forEach(executionInput -> {

            final ProcessingMessage processingMessage = ProcessingMessageUtils.create()
                    .setSatelliteId(executionInput.getSatellite())
                    .setKeyObjectStorage(MessageParameters.EMPTY)
                    .setStoragePath(MessageParameters.EMPTY)
                    .setProductFamily(ProductFamily.S2_L0_DS)
                    .setAllowedActions(getAllowedActions());

            processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);
            processingMessage.getAdditionalFields().put(MessageParameters.RESUBMIT_INFOS, getResubmitInfos(inputProcessingMessage));

            messages.add(processingMessage);
        });

        log.info("Finished creating output messages");

        return messages;
    }

}
