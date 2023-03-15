package eu.csgroup.coprs.ps2.ew.l2ds.service.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.exception.ProcessingException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class L2dsEWMessageService extends EWMessageService<L2ExecutionInput> {

    private final ObjectMapper objectMapper;
    private final ObsBucketProperties bucketProperties;

    public L2dsEWMessageService(ObjectMapper objectMapper, ObsBucketProperties bucketProperties) {
        this.objectMapper = objectMapper;
        this.bucketProperties = bucketProperties;
    }

    @Override
    protected Set<ProcessingMessage> doBuild(L2ExecutionInput executionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String outputFolder) {

        Set<ProcessingMessage> messages = buildCatalogMessages(fileInfosByFamily, executionInput);

        // There should only be 1 DS at that point, otherwise something went quite wrong earlier on
        final String l2ADsPath =
                fileInfosByFamily.get(ProductFamily.S2_L2A_DS)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new ProcessingException("Something went horribly wrong"))
                        .getFullLocalPath();

        final Set<ProcessingMessage> tileMessages = executionInput.getTileList()
                .stream()
                .map(tile -> {

                    final L2ExecutionInput tileInput;
                    try {
                        tileInput = objectMapper.readValue(objectMapper.writeValueAsString(executionInput), L2ExecutionInput.class);
                    } catch (JsonProcessingException e) {
                        throw new ProcessingException("Something went horribly wrong");
                    }

                    tileInput.setTile(tile);
                    tileInput.setL2aDsPath(l2ADsPath);
                    tileInput.setFiles(Set.of(new FileInfo()
                            .setProductFamily(ProductFamily.S2_L1C_TL)
                            .setObsName(tile)
                            .setBucket(bucketProperties.getL1TLBucket())
                            .setLocalName(tile)
                            .setLocalPath(FolderParameters.WORKING_FOLDER_ROOT)));

                    final ProcessingMessage tileMessage = ProcessingMessageUtils.create().setAllowedActions(getAllowedActions());
                    tileMessage.setSatelliteId(executionInput.getSatellite());
                    tileMessage.getAdditionalFields().put(MessageParameters.EXECUTION_INPUT_FIELD, tileInput);
                    return tileMessage;

                })
                .collect(Collectors.toSet());

        messages.addAll(tileMessages);

        return messages;
    }

}
