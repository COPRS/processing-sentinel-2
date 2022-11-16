package eu.csgroup.coprs.ps2.core.ew.service.l1;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class L1EWCleanupService extends EWCleanupService<L1ExecutionInput> {

    protected L1EWCleanupService(CleanupProperties cleanupProperties) {
        super(cleanupProperties);
    }

    @Override
    protected void doPrepare() {
        //
    }

    @Override
    protected void doCleanBefore() {
        //
    }

    @Override
    protected void doCleanAfter(L1ExecutionInput executionInput) {
        //
    }

}
