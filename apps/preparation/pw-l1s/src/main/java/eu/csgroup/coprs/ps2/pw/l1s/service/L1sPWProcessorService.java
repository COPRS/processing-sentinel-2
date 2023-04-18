package eu.csgroup.coprs.ps2.pw.l1s.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripEntity;
import eu.csgroup.coprs.ps2.pw.l1s.service.output.L1sPWMessageService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sDatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sDatastripService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sPWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l1s.service.setup.L1sPWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L1sPWProcessorService extends PWProcessorService<L1ExecutionInput, L1sDatastrip, L1sDatastripEntity, L1sDatastripService> {

    public L1sPWProcessorService(
            L1sPWInputManagementService inputManagementService,
            L1sDatastripManagementService itemManagementService,
            L1sPWExecutionInputService executionInputService,
            L1sPWMessageService messageService
    ) {
        super(inputManagementService, itemManagementService, executionInputService, messageService);
    }

}
