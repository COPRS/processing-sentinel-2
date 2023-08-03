package eu.csgroup.coprs.ps2.ew.l0u.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
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

    public L0uEWProcessorService(
            L0uEWInputService inputService,
            L0uEWSetupService setupService,
            L0uEWExecutionService executionService,
            L0uEWOutputService outputService,
            MissingOutputProperties missingOutputProperties
    ) {
        super(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L0uExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.L0_DS, missingOutputProperties.getL0uDefaultDsCount(), executionInput.getSatellite(), 0, false, missingOutputProperties.getL0uIpfVersion()
        );

        final JobProcessingTaskMissingOutput hktmMissingOutput = buildMissingOutput(
                MissingOutputProductType.HKTM, missingOutputProperties.getL0uDefaultHktmCount(), executionInput.getSatellite(), 0, true, missingOutputProperties.getL0uIpfVersion()
        );

        final JobProcessingTaskMissingOutput sadMissingOutput = buildMissingOutput(
                MissingOutputProductType.SAD, missingOutputProperties.getL0uDefaultSadCount(), executionInput.getSatellite(), 0, true, missingOutputProperties.getL0uIpfVersion()
        );

        return List.of(dsMissingOutput, hktmMissingOutput, sadMissingOutput);
    }

}
