package eu.csgroup.coprs.ps2.core.ew.service.l1;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class L1EWOutputService extends EWOutputService<L1ExecutionInput> {

    protected L1EWOutputService(EWMessageService<L1ExecutionInput> messageService, EWCleanupService<L1ExecutionInput> cleanupService) {
        super(messageService, cleanupService);
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput) {
        return Collections.emptyMap();
    }

}
