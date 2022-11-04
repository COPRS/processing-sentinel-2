package eu.csgroup.coprs.ps2.ew.l1s.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1sMessageService extends EWMessageService<L1ExecutionInput> {

    @Override
    public Set<ProcessingMessage> build(L1ExecutionInput executionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String... options) {

        log.info("Building outgoing messages");

        // Building a single message for L1ab & L1c
        ProcessingMessage preparationMessage = ProcessingMessageUtils.create();
        executionInput.setCustomTaskInputs(getCustomOutputs(executionInput.getOutputFolder()));
        preparationMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, executionInput);

        log.info("Finished building outgoing messages");

        return Set.of(preparationMessage);
    }

    private Set<String> getCustomOutputs(String outputFolder) {
        final Path rootPath = Paths.get(outputFolder);
        return FileOperationUtils.findFoldersInTree(rootPath, S2FileParameters.L1_DS_REGEX)
                .stream().map(path -> path.getFileName().toString() + L1Parameters.TMP_DS_SUFFIX)
                .collect(Collectors.toSet());
    }

}
