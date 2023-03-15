package eu.csgroup.coprs.ps2.pw.l1s.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.pw.service.PWMessageService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class L1sPWMessageService extends PWMessageService<L1ExecutionInput> {

    @Override
    protected EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESTART, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

}
