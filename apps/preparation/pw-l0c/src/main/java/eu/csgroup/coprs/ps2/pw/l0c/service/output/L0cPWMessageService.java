package eu.csgroup.coprs.ps2.pw.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.pw.service.PWMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class L0cPWMessageService extends PWMessageService<L0cExecutionInput> {

    @Override
    protected EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

}
