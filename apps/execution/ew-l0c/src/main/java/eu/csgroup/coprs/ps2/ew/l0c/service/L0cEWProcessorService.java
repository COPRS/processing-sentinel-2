package eu.csgroup.coprs.ps2.ew.l0c.service;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.service.processor.EWProcessorService;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.ew.l0c.service.exec.L0cEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l0c.service.output.L0cEWOutputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWInputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Configuration
public class L0cEWProcessorService extends EWProcessorService<L0cExecutionInput> {

    private static final String IPF_VERSION = "5.0.1";

    public L0cEWProcessorService(
            L0cEWInputService inputService,
            L0cEWSetupService setupService,
            L0cEWExecutionService executionService,
            L0cEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L0cExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.DS, 1, executionInput.getSatellite(), true, IPF_VERSION
        );

        int grCount;
        try {
            grCount = FileOperationUtils.findFoldersInTree(Paths.get(executionInput.getDtFolder()), S2FileParameters.L0C_GR_REGEX).size();
        } catch (FileOperationException e) {
            // Set an arbitrary number if we can't get the real one
            grCount = 720;
        }

        final JobProcessingTaskMissingOutput grMissingOutput = buildMissingOutput(
                MissingOutputProductType.GR, grCount, executionInput.getSatellite(), true, IPF_VERSION
        );

        return List.of(dsMissingOutput, grMissingOutput);
    }

}
