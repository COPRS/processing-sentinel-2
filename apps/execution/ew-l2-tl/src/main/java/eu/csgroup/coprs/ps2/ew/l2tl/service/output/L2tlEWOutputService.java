package eu.csgroup.coprs.ps2.ew.l2tl.service.output;

import eu.csgroup.coprs.ps2.core.ew.service.l2.L2EWOutputService;
import eu.csgroup.coprs.ps2.ew.l2tl.service.setup.L2tlWCleanupService;
import org.springframework.stereotype.Service;

@Service
public class L2tlEWOutputService extends L2EWOutputService {

    protected L2tlEWOutputService(L2tlEWMessageService messageService, L2tlWCleanupService cleanupService, L2tlEWUploadService uploadService) {
        super(messageService, cleanupService, uploadService);
    }

}
