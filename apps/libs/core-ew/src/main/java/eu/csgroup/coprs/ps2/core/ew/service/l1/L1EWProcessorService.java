package eu.csgroup.coprs.ps2.core.ew.service.l1;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;

public abstract class L1EWProcessorService extends EWProcessorService<L1ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    protected L1EWProcessorService(L1EWInputService inputService,
            EWSetupService<L1ExecutionInput> setupService,
            L01EWExecutionService<L1ExecutionInput> executionService,
            L1EWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    protected JobProcessingTaskMissingOutput buildL1MissingOutput(MissingOutputProductType type, Integer count, L1ExecutionInput executionInput) {
        return buildMissingOutput(type, count, executionInput.getSatellite(), 1, true, IPF_VERSION);
    }


}
