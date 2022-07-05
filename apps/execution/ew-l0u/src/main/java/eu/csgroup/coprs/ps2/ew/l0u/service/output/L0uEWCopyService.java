package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Slf4j
@Component
public class L0uEWCopyService {

    private final L0uExecutionProperties l0uExecutionProperties;

    public L0uEWCopyService(L0uExecutionProperties l0uExecutionProperties) {
        this.l0uExecutionProperties = l0uExecutionProperties;
    }

    public String copy() {

        log.info("Copying output files to shared disk");

        final String uuid = UUID.randomUUID().toString();
        final Path destPath = Paths.get(l0uExecutionProperties.getOutputFolderRoot(), uuid);
        final Path rootPath = Paths.get(L0uFolderParameters.L0U_DUMP_PATH);

        final List<Path> dtFolders = FileOperationUtils.findFolders(rootPath, S2FileParameters.DT_REGEX);
        log.info("Found {} DT folders", dtFolders.size());

        dtFolders.forEach(path -> {
            try {
                FileUtils.copyDirectoryToDirectory(path.toFile(), destPath.toFile());
            } catch (IOException e) {
                throw new FileOperationException("Unable to copy files to shared disk", e);
            }
        });

        log.info("Finished copying output files to shared disk");

        return uuid;
    }

}
