package eu.csgroup.coprs.ps2.core.ew.service.l2;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class L2EWOutputService extends EWOutputService<L2ExecutionInput> {

    protected final EWUploadService<L2ExecutionInput> uploadService;

    protected L2EWOutputService(EWMessageService<L2ExecutionInput> messageService, EWCleanupService<L2ExecutionInput> cleanupService,
            EWUploadService<L2ExecutionInput> uploadService
    ) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L2ExecutionInput executionInput, UUID parentUid) {
        return uploadService.upload(executionInput, parentUid);
    }

}
