package eu.csgroup.coprs.ps2.ew.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWProcessorService;
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
public class L1cEWProcessorService extends L1EWProcessorService {
    
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
        if (StringUtils.hasText(executionInput.getTile())) {
            return List.of(
                    buildL1MissingOutput(MissingOutputProductType.L1C_TL, 1, executionInput),
                    buildL1MissingOutput(MissingOutputProductType.L1C_TC, 1, executionInput));
        } else {
            return List.of(buildL1MissingOutput(MissingOutputProductType.L1C_DS, 1, executionInput));
        }

    }

}
