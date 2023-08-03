package eu.csgroup.coprs.ps2.ew.l1sa.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1sabEWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.exec.L1saEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.output.L1saEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L1saEWProcessorService extends L1sabEWProcessorService {

    protected L1saEWProcessorService(
            L1saEWInputService inputService,
            L1saEWSetupService setupService,
            L1saEWExecutionService executionService,
            L1saEWOutputService outputService,
            MissingOutputProperties missingOutputProperties
    ) {
        super(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L1ExecutionInput executionInput) {
        return getL1sabMissingOutputs(executionInput);
    }

}
