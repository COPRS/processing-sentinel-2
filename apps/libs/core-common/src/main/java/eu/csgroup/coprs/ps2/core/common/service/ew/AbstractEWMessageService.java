package eu.csgroup.coprs.ps2.core.common.service.ew;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractEWMessageService<T extends ExecutionInput> {

    public abstract Set<ProcessingMessage> build(T executionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String... options);

    protected Set<ProcessingMessage> buildMessages(Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, T executionInput) {
        Set<ProcessingMessage> messages = new HashSet<>();
        fileInfosByFamily.forEach((productFamily, fileInfoSet) ->
                fileInfoSet.forEach(fileInfo -> {
                    ProcessingMessage processingMessage = ProcessingMessageUtils.create()
                            .setProductFamily(productFamily)
                            .setStoragePath(fileInfo.getObsURL())
                            .setKeyObjectStorage(fileInfo.getObsName())
                            .setSatelliteId(executionInput.getSatellite());
                    messages.add(processingMessage);
                })
        );
        return messages;
    }

}
