package eu.csgroup.coprs.ps2.core.ew.service.l2;

import eu.csgroup.coprs.ps2.core.common.model.l1.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWInputService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class L2EWInputService implements EWInputService<L2ExecutionInput> {

    @Override
    public L2ExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final L2ExecutionInput executionInput = ProcessingMessageUtils.getAdditionalField(
                processingMessage,
                MessageParameters.EXECUTION_INPUT_FIELD,
                L2ExecutionInput.class
        );

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return executionInput;
    }

}
