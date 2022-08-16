package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWMessageService;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class L0uEWMessageService extends EWMessageService<L0uExecutionInput> {

    public Set<ProcessingMessage> build(L0uExecutionInput l0uExecutionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String... options) {

        log.info("Building outgoing messages");

        Set<ProcessingMessage> messages = buildCatalogMessages(fileInfosByFamily, l0uExecutionInput);

        // Building a single message for L0C preparation
        final String outputFolder = options[0];
        ProcessingMessage preparationMessage = ProcessingMessageUtils.create();
        preparationMessage
                .getAdditionalFields()
                .put(
                        MessageParameters.PREPARATION_INPUT_FIELD,
                        new L0cPreparationInput()
                                .setInputFolder(outputFolder)
                                .setSession(l0uExecutionInput.getSession())
                                .setSatellite(l0uExecutionInput.getSatellite())
                                .setStation(l0uExecutionInput.getStation())
                                .setT0PdgsDate(l0uExecutionInput.getT0PdgsDate())
                );
        messages.add(preparationMessage);

        log.info("Finished building outgoing messages");

        return messages;
    }

}
