package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWCleanupService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;


@Service
public class L0uEWOutputService extends EWOutputService<L0uExecutionInput> {

    private final L0uEWUploadService uploadService;
    private final L0uEWCopyService copyService;


    public L0uEWOutputService(L0uEWMessageService messageService, L0uEWCleanupService cleanupService, L0uEWUploadService uploadService, L0uEWCopyService copyService) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
        this.copyService = copyService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L0uExecutionInput executionInput) {
        return uploadService.upload(executionInput);
    }

    @Override
    protected String copy() {
        return copyService.copy();
    }

}
