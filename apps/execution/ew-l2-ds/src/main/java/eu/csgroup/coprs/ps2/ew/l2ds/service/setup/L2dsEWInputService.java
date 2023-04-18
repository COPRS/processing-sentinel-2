package eu.csgroup.coprs.ps2.ew.l2ds.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.l2.L2EWInputService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class L2dsEWInputService extends L2EWInputService {

    @Override
    public Set<String> getTaskInputs(L2ExecutionInput executionInput) {
        return Set.of(executionInput.getDatastrip());
    }

}
