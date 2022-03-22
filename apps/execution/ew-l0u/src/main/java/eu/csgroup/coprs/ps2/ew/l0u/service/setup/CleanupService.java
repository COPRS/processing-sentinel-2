package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessUtils;
import eu.csgroup.coprs.ps2.ew.l0u.settings.FolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;


@Slf4j
@Component
public class CleanupService {

    public void clean() {

        log.info("Cleaning and setting up workspace");

        FileOperationUtils.deleteFiles(FolderParameters.INSTALL_CONF_PATH, "Generic_Archive_Request.xml*");
        FileOperationUtils.deleteFiles(FolderParameters.INSTALL_CONF_PATH, "Production_Request_L0u.xml*");
        FileOperationUtils.deleteFiles(FolderParameters.INSTALL_CONF_PATH, "EISPProcTime2Orbit.log.*.gz");

        if (Files.exists(Paths.get(FolderParameters.INSTALL_ARCHIVE_PATH))) {
            FileOperationUtils.deleteFolderContent(FolderParameters.INSTALL_ARCHIVE_PATH);
        }
        if (Files.exists(Paths.get(FolderParameters.INSTALL_OUTPUT_FILES_PATH))) {
            FileOperationUtils.deleteFolderContent(FolderParameters.INSTALL_OUTPUT_FILES_PATH);
        }

        FileOperationUtils.deleteFolders(Set.of(FolderParameters.INPUT_PATH, FolderParameters.WORKPLANS_PATH));
        FileOperationUtils.createFolders(FolderParameters.WORKSPACE_FOLDERS);

        ProcessUtils.kill("EISPProcessor");
        ProcessUtils.kill("launch_eisp_ing_typ.bash");
        ProcessUtils.kill("launch_eisp.bash");
        ProcessUtils.kill("launch_merge.bash");
        ProcessUtils.kill("launch_telemetry.bash");

        log.info("Finished cleaning and setting up workspace");
    }

}
