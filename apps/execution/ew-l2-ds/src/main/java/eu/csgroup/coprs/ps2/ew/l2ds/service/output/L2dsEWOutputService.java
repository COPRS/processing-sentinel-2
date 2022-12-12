package eu.csgroup.coprs.ps2.ew.l2ds.service.output;

import eu.csgroup.coprs.ps2.core.ew.service.l2.L2EWOutputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWCleanupService;
import org.springframework.stereotype.Service;

@Service
public class L2dsEWOutputService extends L2EWOutputService {

    protected L2dsEWOutputService(L2dsEWMessageService messageService, L2dsEWCleanupService cleanupService, L2dsEWUploadService uploadService) {
        super(messageService, cleanupService, uploadService);
    }

}
