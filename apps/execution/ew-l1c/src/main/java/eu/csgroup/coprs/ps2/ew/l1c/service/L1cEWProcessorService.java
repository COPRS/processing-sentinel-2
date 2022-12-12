package eu.csgroup.coprs.ps2.ew.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1c.service.exec.L1cEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1c.service.output.L1cEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1c.service.setup.L1cEWInputService;
import eu.csgroup.coprs.ps2.ew.l1c.service.setup.L1cEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
@Configuration
public class L1cEWProcessorService extends EWProcessorService<L1ExecutionInput> {

    private static final String IPF_VERSION = "6.1.0";

    protected L1cEWProcessorService(
            L1cEWInputService inputService,
            L1cEWSetupService setupService,
            L1cEWExecutionService executionService,
            L1cEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L1ExecutionInput executionInput) {
        MissingOutputProductType type = StringUtils.hasText(executionInput.getTile()) ? MissingOutputProductType.L1C_TL : MissingOutputProductType.L1C_DS;
        return List.of(buildMissingOutput(type, 1, executionInput.getSatellite(), 1, true, IPF_VERSION));
    }

}
