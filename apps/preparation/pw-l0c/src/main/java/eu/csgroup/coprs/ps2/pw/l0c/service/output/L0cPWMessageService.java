package eu.csgroup.coprs.ps2.pw.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.pw.service.PWMessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class L0cPWMessageService extends PWMessageService<L0cExecutionInput> {

    @Override
    protected EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

    @Override
    protected Map<String, Object> getResubmitInfos(ProcessingMessage processingMessage) {
        return Map.of(
                MessageParameters.PREPARATION_INPUT_FIELD,
                ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.PREPARATION_INPUT_FIELD, L0cPreparationInput.class)
        );
    }

}
