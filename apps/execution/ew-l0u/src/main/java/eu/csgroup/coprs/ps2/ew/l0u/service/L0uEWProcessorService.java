package eu.csgroup.coprs.ps2.ew.l0u.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l0u.service.exec.L0uEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l0u.service.output.L0uEWOutputService;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWInputService;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L0uEWProcessorService extends EWProcessorService<L0uExecutionInput> {

    private static final String IPF_VERSION = "5.0.1";

    public L0uEWProcessorService(
            L0uEWInputService inputService,
            L0uEWSetupService setupService,
            L0uEWExecutionService executionService,
            L0uEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L0uExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.L0_DS, 6, executionInput.getSatellite(), 0, false, IPF_VERSION
        );

        final JobProcessingTaskMissingOutput hktmMissingOutput = buildMissingOutput(
                MissingOutputProductType.HKTM, 1, executionInput.getSatellite(), 0, false, IPF_VERSION
        );

        final JobProcessingTaskMissingOutput sadMissingOutput = buildMissingOutput(
                MissingOutputProductType.SAD, 2, executionInput.getSatellite(), 0, false, IPF_VERSION
        );

        return List.of(dsMissingOutput, hktmMissingOutput, sadMissingOutput);
    }

}
