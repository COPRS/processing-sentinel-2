package eu.csgroup.coprs.ps2.pw.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.service.processor.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.pw.l0c.service.output.L0cPWMessageService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.DatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.DatastripService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cPWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0c.service.setup.L0cPWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L0cPWProcessorService extends PWProcessorService<L0cExecutionInput, Datastrip, DatastripService> {

    public L0cPWProcessorService(
            L0cPWInputManagementService inputManagementService,
            DatastripManagementService managementService,
            L0cPWExecutionInputService executionInputService,
            L0cPWMessageService messageService
    ) {
        super(inputManagementService, managementService, executionInputService, messageService);
    }

}
