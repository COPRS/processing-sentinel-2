package eu.csgroup.coprs.ps2.pw.l2.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripEntity;
import eu.csgroup.coprs.ps2.pw.l2.service.output.L2PWMessageService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2PWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l2.service.setup.L2PWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L2PWProcessorService extends PWProcessorService<L2ExecutionInput, L2Datastrip, L2DatastripEntity, L2DatastripService> {

    public L2PWProcessorService(
            L2PWInputManagementService inputManagementService,
            L2DatastripManagementService itemManagementService,
            L2PWExecutionInputService executionInputService,
            L2PWMessageService messageService
    ) {
        super(inputManagementService, itemManagementService, executionInputService, messageService);
    }

}
