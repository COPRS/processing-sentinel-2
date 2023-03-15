package eu.csgroup.coprs.ps2.core.ew.service.l1;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;

import java.util.Map;
import java.util.Set;

public abstract class L1sEWMessageService extends EWMessageService<L1ExecutionInput> {

    protected abstract Set<String> getCustomOutputs(String outputFolder);

    @Override
    protected Set<ProcessingMessage> doBuild(L1ExecutionInput executionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String outputFolder) {

        ProcessingMessage processingMessage = ProcessingMessageUtils.create().setAllowedActions(getAllowedActions());
        processingMessage.setSatelliteId(executionInput.getSatellite());
        executionInput.setCustomTaskInputs(getCustomOutputs(executionInput.getOutputFolder()));
        processingMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);

        return Set.of(processingMessage);
    }

}
