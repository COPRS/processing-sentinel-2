package eu.csgroup.coprs.ps2.ew.l1c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class L1cEWMessageService extends EWMessageService<L1ExecutionInput> {

    @Override
    protected Set<ProcessingMessage> doBuild(L1ExecutionInput executionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String... options) {
        return buildCatalogMessages(fileInfosByFamily, executionInput);
    }

}
