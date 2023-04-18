package eu.csgroup.coprs.ps2.ew.l1ab.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.Level;
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
import java.util.ArrayList;
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

        List<TaskMissingOutput> missingOutputs = new ArrayList<>();

        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1A)) {

            missingOutputs.add(buildMissingOutput(MissingOutputProductType.L1A_DS, 1, executionInput.getSatellite(), 1, true, IPF_VERSION));

            int grCountA = FileOperationUtils.findFoldersInTree(Paths.get(executionInput.getOutputFolder()), S2FileParameters.L1A_GR_REGEX).size();
            missingOutputs.add(buildMissingOutput(MissingOutputProductType.L1A_GR, grCountA, executionInput.getSatellite(), 1, true, IPF_VERSION));
        }
        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1B)) {

            missingOutputs.add(buildMissingOutput(MissingOutputProductType.L1B_DS, 1, executionInput.getSatellite(), 1, true, IPF_VERSION));

            int grCountB = FileOperationUtils.findFoldersInTree(Paths.get(executionInput.getOutputFolder()), S2FileParameters.L1B_GR_REGEX).size();
            missingOutputs.add(buildMissingOutput(MissingOutputProductType.L1B_GR, grCountB, executionInput.getSatellite(), 1, true, IPF_VERSION));
        }

        return missingOutputs;
    }

}
