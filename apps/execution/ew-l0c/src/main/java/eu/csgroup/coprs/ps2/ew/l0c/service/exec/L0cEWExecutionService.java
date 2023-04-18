package eu.csgroup.coprs.ps2.ew.l0c.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l01.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class L0cEWExecutionService extends L01EWExecutionService<L0cExecutionInput> {

    private static final List<OrchestratorMode> L0C_TASKS = List.of(OrchestratorMode.L0C, OrchestratorMode.OLQC_L0DS, OrchestratorMode.OLQC_L0GR);

    public L0cEWExecutionService(SharedProperties sharedProperties) {
        super(sharedProperties);
    }

    @Override
    public void processing(L0cExecutionInput executionInput, UUID parentTaskUid) {

        log.info("Starting L0c processing");

        L0C_TASKS.forEach(orchestratorMode -> runMode(executionInput, parentTaskUid, orchestratorMode));

        log.info("Finished L0c processing");
    }

    @Override
    public String getLevel() {
        return "L0c";
    }

}
