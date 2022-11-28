package eu.csgroup.coprs.ps2.ew.l2tl.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.exec.L2tlEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.output.L2tlEWOutputService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.setup.L2tlEWInputService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.setup.L2tlEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L2tlEWProcessorService extends EWProcessorService<L2ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    protected L2tlEWProcessorService(
            L2tlEWInputService inputService,
            L2tlEWSetupService setupService,
            L2tlEWExecutionService executionService,
            L2tlEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L2ExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput missingOutput = buildMissingOutput(
                MissingOutputProductType.L2_TL, 1, executionInput.getSatellite(), true, IPF_VERSION
        );

        return List.of(missingOutput);
    }

}
