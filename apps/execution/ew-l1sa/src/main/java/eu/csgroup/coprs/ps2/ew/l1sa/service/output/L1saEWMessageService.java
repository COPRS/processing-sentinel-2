package eu.csgroup.coprs.ps2.ew.l1sa.service.output;

import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1sEWMessageService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class L1saEWMessageService extends L1sEWMessageService {

    @Override
    protected Set<String> getCustomOutputs(String outputFolder) {
        final Path rootPath = Paths.get(outputFolder);
        return FileOperationUtils.findFoldersInTree(rootPath, S2FileParameters.L1_DS_REGEX)
                .stream()
                .map(path -> path.getFileName().toString() + L1Parameters.TMP_DS_SUFFIX_L1SA)
                .collect(Collectors.toSet());
    }

}
