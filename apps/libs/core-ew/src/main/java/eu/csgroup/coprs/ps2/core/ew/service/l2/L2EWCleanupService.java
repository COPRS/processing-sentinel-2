package eu.csgroup.coprs.ps2.core.ew.service.l2;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class L2EWCleanupService extends EWCleanupService<L2ExecutionInput> {

    protected L2EWCleanupService(CleanupProperties cleanupProperties) {
        super(cleanupProperties);
    }

}
