package eu.csgroup.coprs.ps2.ew.l1sa.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.exec.L1saEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.output.L1saEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L1saEWProcessorService extends EWProcessorService<L1ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    protected L1saEWProcessorService(
            L1saEWInputService inputService,
            L1saEWSetupService setupService,
            L1saEWExecutionService executionService,
            L1saEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L1ExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1A_DS, 1, executionInput.getSatellite(), 1, false, IPF_VERSION
        );

        int grCount = (int) executionInput.getFiles().stream().filter(fileInfo -> fileInfo.getProductFamily().equals(ProductFamily.S2_L0_GR)).count();

        final JobProcessingTaskMissingOutput grMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1A_GR, grCount, executionInput.getSatellite(), 1, false, IPF_VERSION
        );

        return List.of(dsMissingOutput, grMissingOutput);
    }

}
