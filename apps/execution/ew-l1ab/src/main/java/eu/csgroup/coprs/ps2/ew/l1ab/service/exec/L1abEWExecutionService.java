package eu.csgroup.coprs.ps2.ew.l1ab.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l01.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.common.model.processing.Level;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class L1abEWExecutionService extends L01EWExecutionService<L1ExecutionInput> {

    private static final List<OrchestratorMode> L1A_TASKS = List.of(OrchestratorMode.L1A_FORMAT_DS, OrchestratorMode.L1A_FORMAT_GR);
    private static final List<OrchestratorMode> L1B_TASKS = List.of(OrchestratorMode.OLQC_L1BDS, OrchestratorMode.L1B_FORMAT_GR);

    protected L1abEWExecutionService(SharedProperties sharedProperties) {
        super(sharedProperties);
    }

    @Override
    public void processing(L1ExecutionInput executionInput, UUID parentTaskUid) {

        log.info("Starting L1ab processing");

        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1A)) {
            L1A_TASKS.forEach(orchestratorMode -> runMode(executionInput, parentTaskUid, orchestratorMode));
        }

        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1B)) {
            L1B_TASKS.forEach(orchestratorMode -> runMode(executionInput, parentTaskUid, orchestratorMode));
        }

        log.info("Finished L1ab processing");
    }

    @Override
    public String getLevel() {
        return "L1ab";
    }

}
