package eu.csgroup.coprs.ps2.pw.l2.service.output;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.pw.service.PWMessageService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class L2PWMessageService extends PWMessageService<L2ExecutionInput> {

    @Override
    protected EventAction[] getAllowedActions() {
        return List.of(EventAction.NO_ACTION, EventAction.RESUBMIT).toArray(new EventAction[0]);
    }

}
