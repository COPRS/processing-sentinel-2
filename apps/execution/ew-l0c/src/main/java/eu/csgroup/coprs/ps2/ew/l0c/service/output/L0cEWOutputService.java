package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWCleanupService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class L0cEWOutputService extends EWOutputService<L0cExecutionInput> {

    private final L0cEWUploadService uploadService;

    public L0cEWOutputService(L0cEWMessageService messageService, L0cEWCleanupService cleanupService, L0cEWUploadService uploadService) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L0cExecutionInput executionInput) {
        return uploadService.upload(executionInput);
    }

}
