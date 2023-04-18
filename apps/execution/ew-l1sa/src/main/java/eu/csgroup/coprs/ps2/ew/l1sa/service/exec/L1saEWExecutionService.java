package eu.csgroup.coprs.ps2.ew.l1sa.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l01.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class L1saEWExecutionService extends L01EWExecutionService<L1ExecutionInput> {

    public L1saEWExecutionService(SharedProperties sharedProperties) {
        super(sharedProperties);
    }

    @Override
    public void processing(L1ExecutionInput executionInput, UUID parentTaskUid) {

        log.info("Starting L1S processing");

        runMode(executionInput, parentTaskUid, OrchestratorMode.L1A);

        log.info("Finished L1s processing");
    }

    @Override
    public String getLevel() {
        return "L1sa";
    }

}
