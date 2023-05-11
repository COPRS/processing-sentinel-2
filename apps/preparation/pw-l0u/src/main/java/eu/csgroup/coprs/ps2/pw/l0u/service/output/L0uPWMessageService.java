package eu.csgroup.coprs.ps2.pw.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.pw.service.PWMessageService;
import eu.csgroup.coprs.ps2.core.pw.settings.ResubmitMessageParameters;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class L0uPWMessageService extends PWMessageService<L0uExecutionInput> {

    @Override
    protected EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESTART, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

    @Override
    protected Map<String, Object> getResubmitInfos(ProcessingMessage processingMessage) {
        return Map.of(
                ResubmitMessageParameters.MISSION_ID, processingMessage.getMissionId(),
                ResubmitMessageParameters.PRODUCT_FAMILY, processingMessage.getProductFamily(),
                ResubmitMessageParameters.KEY_OBJECT_STORAGE, processingMessage.getKeyObjectStorage(),
                ResubmitMessageParameters.SATELLITE_ID, processingMessage.getSatelliteId(),
                ResubmitMessageParameters.T0_PDGS_DATE, ProcessingMessageUtils.getT0PdgsDate(processingMessage)
        );


    }

}
