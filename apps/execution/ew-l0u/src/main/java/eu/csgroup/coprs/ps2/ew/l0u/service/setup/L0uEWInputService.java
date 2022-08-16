package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWInputService;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component
public class L0uEWInputService implements EWInputService<L0uExecutionInput> {

    @Override
    public L0uExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final L0uExecutionInput l0uExecutionInput = ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.EXECUTION_INPUT_FIELD, L0uExecutionInput.class);

        if (l0uExecutionInput.getJobOrders().size() != 1) {
            throw new InvalidMessageException("Invalid Job Order count");
        }

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return l0uExecutionInput;
    }

    @Override
    public Set<String> getTaskInputs(L0uExecutionInput executionInput) {
        return executionInput.getFiles().stream().map(FileInfo::getObsName).collect(Collectors.toSet());
    }

}
