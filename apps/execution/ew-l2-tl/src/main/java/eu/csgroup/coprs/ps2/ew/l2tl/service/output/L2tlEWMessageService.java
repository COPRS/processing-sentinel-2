package eu.csgroup.coprs.ps2.ew.l2tl.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class L2tlEWMessageService extends EWMessageService<L2ExecutionInput> {

    @Override
    protected Set<ProcessingMessage> doBuild(L2ExecutionInput executionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String outputFolder) {
        return buildCatalogMessages(fileInfosByFamily, executionInput);
    }

}
