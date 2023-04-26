package eu.csgroup.coprs.ps2.ew.l1sb.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1sabEWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.exec.L1sbEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.output.L1sbEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L1sbEWProcessorService extends L1sabEWProcessorService {

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
        return getL1sabMissingOutputs(executionInput);
    }

}
