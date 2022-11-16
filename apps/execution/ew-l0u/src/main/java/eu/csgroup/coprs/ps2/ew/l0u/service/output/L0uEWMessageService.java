package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class L0uEWMessageService extends EWMessageService<L0uExecutionInput> {

    private final L0uExecutionProperties executionProperties;

    public L0uEWMessageService(L0uExecutionProperties executionProperties) {
        this.executionProperties = executionProperties;
    }

    @Override
    protected Set<ProcessingMessage> doBuild(L0uExecutionInput l0uExecutionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String... options) {

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
                                .setCustomTaskInputs(getCustomOutputs(outputFolder))
                );
        messages.add(preparationMessage);

        return messages;
    }

    private Set<String> getCustomOutputs(String outputFolder) {
        final Path l0uPath = Paths.get(executionProperties.getOutputFolderRoot(), outputFolder);
        return FileOperationUtils.findFolders(l0uPath, S2FileParameters.DT_REGEX)
                .stream()
                .map(path -> FileOperationUtils.findFolders(path.resolve("DS"), S2FileParameters.L0U_DS_REGEX))
                .flatMap(Collection::stream)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toSet());
    }

}
