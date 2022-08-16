package eu.csgroup.coprs.ps2.ew.l0u.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.service.processor.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l0u.service.exec.L0uEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l0u.service.output.L0uEWOutputService;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWInputService;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class L0uEWProcessorService extends EWProcessorService<L0uExecutionInput> {

    public L0uEWProcessorService(
            L0uEWInputService inputService,
            L0uEWSetupService setupService,
            L0uEWExecutionService executionService,
            L0uEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

}
