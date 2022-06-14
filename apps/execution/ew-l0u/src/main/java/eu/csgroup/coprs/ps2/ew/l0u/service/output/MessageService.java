package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class MessageService {

    public Set<ProcessingMessage> build(String outputFolder, L0uExecutionInput l0uExecutionInput, Map<ProductFamily, Set<FileInfo>> auxFileInfosByFamily) {

        log.info("Building outgoing messages");

        Set<ProcessingMessage> messages = new HashSet<>();

        // Building messages for Catalog - one for each AUX file produced
        auxFileInfosByFamily.forEach((productFamily, fileInfoSet) ->
                fileInfoSet.forEach(fileInfo -> {
                    ProcessingMessage processingMessage = ProcessingMessageUtils.create()
                            .setProductFamily(productFamily)
                            .setStoragePath(StringUtils.substringBeforeLast(fileInfo.getObsURL(), "/"))
                            .setKeyObjectStorage(fileInfo.getObsName())
                            .setSatelliteId(l0uExecutionInput.getSatellite());
                    messages.add(processingMessage);
                })
        );

        // Building a single message for L0C preparation
        ProcessingMessage preparationMessage = ProcessingMessageUtils.create();
        preparationMessage
                .getAdditionalFields()
                .put(
                        MessageParameters.PREPARATION_INPUT_FIELD,
                        new L0cPreparationInput()
                                .setSession(l0uExecutionInput.getSession())
                                .setSatellite(l0uExecutionInput.getSatellite())
                                .setStation(l0uExecutionInput.getStation())
                                .setInputFolder(outputFolder)
                );
        messages.add(preparationMessage);

        log.info("Finished building outgoing messages");

        return messages;
    }

}
