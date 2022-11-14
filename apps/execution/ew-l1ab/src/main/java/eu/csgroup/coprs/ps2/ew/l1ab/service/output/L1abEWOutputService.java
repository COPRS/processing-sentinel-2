package eu.csgroup.coprs.ps2.ew.l1ab.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.L1EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1ab.config.L1abExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class L1abEWOutputService extends L1EWOutputService {

    private final L1abEWUploadService uploadService;

    public L1abEWOutputService(L1abEWMessageService messageService, L1abEWCleanupService cleanupService, L1abExecutionProperties executionProperties,
            L1abEWUploadService uploadService
    ) {
        super(messageService, cleanupService, executionProperties);
        this.uploadService = uploadService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput) {
        return uploadService.upload(executionInput);
    }

}
