package eu.csgroup.coprs.ps2.ew.l1sa.service.output;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class L1saEWOutputService extends L1EWOutputService {

    protected L1saEWOutputService(L1saEWMessageService messageService, L1saEWCleanupService cleanupService, SharedProperties sharedProperties) {
        super(messageService, cleanupService, sharedProperties);
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput) {
        return Collections.emptyMap();
    }

}
