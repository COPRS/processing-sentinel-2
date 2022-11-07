package eu.csgroup.coprs.ps2.ew.l1ab.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.Level;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.exec.L1abEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.output.L1abEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWInputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Configuration
public class L1abEWProcessorService extends EWProcessorService<L1ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    public L1abEWProcessorService(
            L1abEWInputService inputService,
            L1abEWSetupService setupService,
            L1abEWExecutionService executionService,
            L1abEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L1ExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1_DS, 1, executionInput.getSatellite(), true, IPF_VERSION
        );

        int grCount = 0;
        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1A)) {
            grCount += FileOperationUtils.findFoldersInTree(Paths.get(executionInput.getOutputFolder()), S2FileParameters.L1A_GR_REGEX).size();
        }
        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1B)) {
            grCount += FileOperationUtils.findFoldersInTree(Paths.get(executionInput.getOutputFolder()), S2FileParameters.L1B_GR_REGEX).size();
        }

        final JobProcessingTaskMissingOutput grMissingOutput = buildMissingOutput(
                MissingOutputProductType.L1_GR, grCount, executionInput.getSatellite(), true, IPF_VERSION
        );

        return List.of(dsMissingOutput, grMissingOutput);
    }

}
