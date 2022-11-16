package eu.csgroup.coprs.ps2.ew.l1sa.service.exec;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l1.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sa.config.L1saExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class L1saEWExecutionService extends L1EWExecutionService<L1ExecutionInput> {

    public L1saEWExecutionService(L1saExecutionProperties executionProperties) {
        super(executionProperties);
    }

    @Override
    public void processing(L1ExecutionInput executionInput, UUID parentTaskUid) {

        log.info("Starting L1S processing");

        runMode(executionInput, parentTaskUid, OrchestratorMode.L1A, executionProperties);

        log.info("Finished L1s processing");
    }

    @Override
    public String getLevel() {
        return "L1sa";
    }

}
