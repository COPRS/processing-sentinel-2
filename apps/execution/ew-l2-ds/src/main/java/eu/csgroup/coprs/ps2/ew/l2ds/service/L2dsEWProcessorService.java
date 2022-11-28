package eu.csgroup.coprs.ps2.ew.l2ds.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.exec.L2dsEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.output.L2dsEWOutputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWInputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L2dsEWProcessorService extends EWProcessorService<L2ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    protected L2dsEWProcessorService(
            L2dsEWInputService inputService,
            L2dsEWSetupService setupService,
            L2dsEWExecutionService executionService,
            L2dsEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L2ExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput missingOutput = buildMissingOutput(
                MissingOutputProductType.L2_DS, 1, executionInput.getSatellite(), true, IPF_VERSION
        );

        return List.of(missingOutput);
    }

}
