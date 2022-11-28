package eu.csgroup.coprs.ps2.ew.l1sa.service.output;

import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1saEWOutputService extends L1EWOutputService {

    protected L1saEWOutputService(L1saEWMessageService messageService, L1saEWCleanupService cleanupService) {
        super(messageService, cleanupService);
    }

}
