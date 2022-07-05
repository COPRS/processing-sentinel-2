package eu.csgroup.coprs.ps2.pw.l0u.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.service.processor.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.service.output.L0uPWMessageService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.L0uPWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionManagementService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionService;
import eu.csgroup.coprs.ps2.pw.l0u.service.setup.L0uPWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L0uPWProcessorService extends PWProcessorService<L0uExecutionInput, Session, SessionService> {

    public L0uPWProcessorService(
            L0uPWInputManagementService inputManagementService,
            SessionManagementService itemManagementService,
            L0uPWExecutionInputService executionInputService,
            L0uPWMessageService messageService
    ) {
        super(inputManagementService, itemManagementService, executionInputService, messageService);
    }

}
