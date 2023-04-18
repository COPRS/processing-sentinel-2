package eu.csgroup.coprs.ps2.ew.l1c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWCleanupService;
import org.springframework.stereotype.Service;

@Service
public class L1cEWCleanupService extends L1EWCleanupService {

    protected L1cEWCleanupService(CleanupProperties cleanupProperties) {
        super(cleanupProperties);
    }

}
