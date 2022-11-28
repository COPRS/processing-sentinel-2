package eu.csgroup.coprs.ps2.ew.l1sb.service.output;

import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sb.service.setup.L1sbEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1sbEWOutputService extends L1EWOutputService {

    protected L1sbEWOutputService(L1sbEWMessageService messageService, L1sbEWCleanupService cleanupService) {
        super(messageService, cleanupService);
    }

}
