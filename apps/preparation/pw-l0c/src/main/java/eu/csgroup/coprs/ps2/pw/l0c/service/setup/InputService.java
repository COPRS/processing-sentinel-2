package eu.csgroup.coprs.ps2.pw.l0c.service.setup;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidInputException;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class InputService {

    private final ObjectMapper objectMapper;
    private final L0cPreparationProperties l0cPreparationProperties;

    public InputService(ObjectMapper objectMapper, L0cPreparationProperties l0cPreparationProperties) {
        this.objectMapper = objectMapper;
        this.l0cPreparationProperties = l0cPreparationProperties;
    }

    public L0cPreparationInput extractInput(ProcessingMessage processingMessage) {

        log.info("Extracting preparation input from message: {}", processingMessage);

        final Map<String, Object> additionalFields = processingMessage.getAdditionalFields();

        if (!additionalFields.containsKey(MessageParameters.PREPARATION_INPUT_FIELD) || additionalFields.get(MessageParameters.PREPARATION_INPUT_FIELD) == null) {
            throw new InvalidMessageException("Missing preparation input field");
        }

        final L0cPreparationInput l0cPreparationInput = objectMapper.convertValue(additionalFields.get(MessageParameters.PREPARATION_INPUT_FIELD), L0cPreparationInput.class);

        if (l0cPreparationInput.getInputFolder().isBlank() || l0cPreparationInput.getSession().isBlank()) {
            throw new InvalidMessageException("Invalid Preparation Data");
        }

        return l0cPreparationInput;
    }

    public List<Path> getDatastrips(String inputFolder) {

        List<Path> datastripPaths = new ArrayList<>();

        final Path l0uDumpPath = Paths.get(l0cPreparationProperties.getInputFolderRoot(), inputFolder);

        FileOperationUtils.findFolders(l0uDumpPath, S2FileParameters.DT_REGEX)
                .forEach(path -> {
                    final List<Path> datastripFolders = FileOperationUtils.findFolders(path.resolve("DS"), S2FileParameters.L0U_DS_REGEX);
                    // We should have exactly one DS in each DT
                    if (datastripFolders.size() != 1) {
                        throw new InvalidInputException("Invalid number of DS in DT: " + path);
                    }
                    datastripPaths.add(datastripFolders.get(0));
                });

        return datastripPaths;
    }

}
