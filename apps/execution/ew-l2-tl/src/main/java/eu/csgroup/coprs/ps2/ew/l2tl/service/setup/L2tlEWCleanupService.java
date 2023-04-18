package eu.csgroup.coprs.ps2.ew.l2tl.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.ew.service.l2.L2EWCleanupService;
import org.springframework.stereotype.Service;

@Service
public class L2tlEWCleanupService extends L2EWCleanupService {

    protected L2tlEWCleanupService(CleanupProperties cleanupProperties) {
        super(cleanupProperties);
    }

}
