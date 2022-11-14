package eu.csgroup.coprs.ps2.ew.l1s.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.L1EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1s.config.L1sExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l1s.service.setup.L1sEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class L1sEWOutputService extends L1EWOutputService {

    protected L1sEWOutputService(L1sEWMessageService messageService, L1sEWCleanupService cleanupService, L1sExecutionProperties executionProperties) {
        super(messageService, cleanupService, executionProperties);
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput) {
        return Collections.emptyMap();
    }

}
