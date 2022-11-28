package eu.csgroup.coprs.ps2.ew.l1c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1c.service.setup.L1cEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class L1cEWOutputService extends L1EWOutputService {

    private final L1cEWUploadService uploadService;

    protected L1cEWOutputService(L1cEWMessageService messageService, L1cEWCleanupService cleanupService, L1cEWUploadService uploadService) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput) {
        return uploadService.upload(executionInput);
    }

}
