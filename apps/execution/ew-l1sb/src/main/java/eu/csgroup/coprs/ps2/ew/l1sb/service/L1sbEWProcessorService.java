package eu.csgroup.coprs.ps2.ew.l1sb.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.exec.L1sbEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.output.L1sbEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L1sbEWProcessorService extends EWProcessorService<L1ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    protected L1sbEWProcessorService(
            L1sbEWInputService inputService,
            L1sbEWSetupService setupService,
            L1sbEWExecutionService executionService,
            L1sbEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L1ExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1B_DS, 1, executionInput.getSatellite(), 1, false, IPF_VERSION
        );

        int grCount = (int) executionInput.getFiles().stream().filter(fileInfo -> fileInfo.getProductFamily().equals(ProductFamily.S2_L0_GR)).count();

        final JobProcessingTaskMissingOutput grMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1B_GR, grCount, executionInput.getSatellite(), 1, false, IPF_VERSION
        );

        return List.of(dsMissingOutput, grMissingOutput);
    }

}
