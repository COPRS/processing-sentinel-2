package eu.csgroup.coprs.ps2.ew.l1s.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1s.service.exec.L1sEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1s.service.output.L1sEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1s.service.setup.L1sEWInputService;
import eu.csgroup.coprs.ps2.ew.l1s.service.setup.L1sEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L1sEWProcessorService extends EWProcessorService<L1ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    protected L1sEWProcessorService(
            L1sEWInputService inputService,
            L1sEWSetupService setupService,
            L1sEWExecutionService executionService,
            L1sEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L1ExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1_DS, 1, executionInput.getSatellite(), true, IPF_VERSION
        );

        int grCount = (int) executionInput.getFiles().stream().filter(fileInfo -> fileInfo.getProductFamily().equals(ProductFamily.S2_L0_GR)).count();

        final JobProcessingTaskMissingOutput grMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1_GR, grCount, executionInput.getSatellite(), true, IPF_VERSION
        );

        return List.of(dsMissingOutput, grMissingOutput);
    }

}
