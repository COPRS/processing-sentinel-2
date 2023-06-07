package eu.csgroup.coprs.ps2.pw.l2.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import eu.csgroup.coprs.ps2.core.pw.model.ResubmitMessage;
import eu.csgroup.coprs.ps2.core.pw.service.PWMessageService;
import eu.csgroup.coprs.ps2.core.pw.settings.ResubmitMessageParameters;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class L2PWMessageService extends PWMessageService<L2ExecutionInput> {

    L2DatastripService itemService;

    public L2PWMessageService(L2DatastripService itemService) {
        this.itemService = itemService;
    }

    @Override
    protected EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

    @Override
    protected Map<String, Object> getResubmitInfos(ProcessingMessage processingMessage) {

        ResubmitMessage message = itemService.read(ObsUtils.keyToName(processingMessage.getKeyObjectStorage())).getResubmitMessage();
        return Map.of(
                ResubmitMessageParameters.MISSION_ID, message.getMissionId(),
                ResubmitMessageParameters.PRODUCT_FAMILY, message.getProductFamily(),
                ResubmitMessageParameters.KEY_OBJECT_STORAGE, message.getKeyObjectStorage(),
                ResubmitMessageParameters.SATELLITE_ID, message.getSatelliteId(),
                ResubmitMessageParameters.T0_PDGS_DATE, message.getT0PdgsDate(),
                ResubmitMessageParameters.STORAGE_PATH, message.getStoragePath()
        );
    }

}
