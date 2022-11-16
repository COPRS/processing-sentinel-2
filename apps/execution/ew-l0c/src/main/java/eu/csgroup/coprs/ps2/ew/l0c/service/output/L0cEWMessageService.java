package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class L0cEWMessageService extends EWMessageService<L0cExecutionInput> {

    @Override
    protected Set<ProcessingMessage> doBuild(L0cExecutionInput l0cExecutionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String... options) {
        return buildCatalogMessages(fileInfosByFamily, l0cExecutionInput);
    }

}
