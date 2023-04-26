package eu.csgroup.coprs.ps2.ew.l1ab.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1sabEWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.exec.L1abEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.output.L1abEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWInputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L1abEWProcessorService extends L1sabEWProcessorService {

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
        return getL1abMissingOutput(executionInput);
    }

}
