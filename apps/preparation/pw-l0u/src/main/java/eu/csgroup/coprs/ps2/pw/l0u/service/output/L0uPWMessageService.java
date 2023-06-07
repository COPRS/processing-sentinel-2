package eu.csgroup.coprs.ps2.pw.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import eu.csgroup.coprs.ps2.core.common.utils.SessionUtils;
import eu.csgroup.coprs.ps2.core.pw.model.ResubmitMessage;
import eu.csgroup.coprs.ps2.core.pw.service.PWMessageService;
import eu.csgroup.coprs.ps2.core.pw.settings.ResubmitMessageParameters;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class L0uPWMessageService extends PWMessageService<L0uExecutionInput> {

    SessionService itemService;

    public L0uPWMessageService(SessionService itemService) {
        this.itemService = itemService;
    }

    @Override
    protected EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESTART, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

    @Override
    protected Map<String, Object> getResubmitInfos(ProcessingMessage processingMessage) {
        ResubmitMessage resubmitMessage = itemService.read(
                        SessionUtils.sessionFromFilename(ObsUtils.keyToName(processingMessage.getKeyObjectStorage())))
                .getResubmitMessage();

        return Map.of(
                ResubmitMessageParameters.MISSION_ID, resubmitMessage.getMissionId(),
                ResubmitMessageParameters.PRODUCT_FAMILY, resubmitMessage.getProductFamily(),
                ResubmitMessageParameters.KEY_OBJECT_STORAGE, resubmitMessage.getKeyObjectStorage(),
                ResubmitMessageParameters.SATELLITE_ID, resubmitMessage.getSatelliteId(),
                ResubmitMessageParameters.T0_PDGS_DATE, resubmitMessage.getT0PdgsDate()
        );


    }

}
