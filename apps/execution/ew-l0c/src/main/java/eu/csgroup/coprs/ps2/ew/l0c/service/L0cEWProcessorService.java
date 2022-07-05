package eu.csgroup.coprs.ps2.ew.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.service.processor.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l0c.service.exec.L0cEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l0c.service.output.L0cEWOutputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWInputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class L0cEWProcessorService extends EWProcessorService<L0cExecutionInput> {

    public L0cEWProcessorService(
            L0cEWInputService inputService,
            L0cEWSetupService setupService,
            L0cEWExecutionService executionService,
            L0cEWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

}
