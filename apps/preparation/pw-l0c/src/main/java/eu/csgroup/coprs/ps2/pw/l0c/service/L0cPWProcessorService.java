package eu.csgroup.coprs.ps2.pw.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripEntity;
import eu.csgroup.coprs.ps2.pw.l0c.service.output.L0cPWMessageService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cDatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cDatastripService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cPWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0c.service.setup.L0cPWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L0cPWProcessorService extends PWProcessorService<L0cExecutionInput, L0cDatastrip, L0cDatastripEntity, L0cDatastripService> {

    public L0cPWProcessorService(
            L0cPWInputManagementService inputManagementService,
            L0cDatastripManagementService managementService,
            L0cPWExecutionInputService executionInputService,
            L0cPWMessageService messageService
    ) {
        super(inputManagementService, managementService, executionInputService, messageService);
    }

}
