package eu.csgroup.coprs.ps2.ew.l1c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWInputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1cEWInputService extends L1EWInputService {

    @Override
    public Set<String> getTaskInputs(L1ExecutionInput executionInput) {
        return executionInput.getCustomTaskInputs()
                .stream().filter(s -> s.contains("L1C"))
                .collect(Collectors.toSet());
    }

}
