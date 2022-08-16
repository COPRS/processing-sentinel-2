package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessUtils;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;


@Slf4j
@Component
public class L0uEWCleanupService {

    private static final Set<String> PROCESSES = Set.of(
            "EISPProcessor",
            "launch_eisp_ing_typ.bash",
            "launch_eisp.bash",
            "launch_merge.bash",
            "launch_telemetry.bash"
    );

    public void cleanAndPrepare() {

        log.info("Cleaning and setting up workspace");

        deleteFolders();
        killProcesses();

        FileOperationUtils.createFolders(L0uFolderParameters.WORKSPACE_FOLDERS);

        log.info("Finished cleaning and setting up workspace");
    }

    public void clean() {

        log.info("Cleaning up workspace");

        deleteFolders();
        killProcesses();

        log.info("Finished cleaning up workspace");
    }

    private void deleteFolders() {

        FileOperationUtils.deleteFiles(L0uFolderParameters.INSTALL_CONF_PATH, "Generic_Archive_Request.xml*");
        FileOperationUtils.deleteFiles(L0uFolderParameters.INSTALL_CONF_PATH, "Production_Request_L0u.xml*");
        FileOperationUtils.deleteFiles(L0uFolderParameters.INSTALL_CONF_PATH, "EISPProcTime2Orbit.log.*.gz");

        if (Files.exists(Paths.get(L0uFolderParameters.INSTALL_ARCHIVE_PATH))) {
            FileOperationUtils.deleteFolderContent(L0uFolderParameters.INSTALL_ARCHIVE_PATH);
        }
        if (Files.exists(Paths.get(L0uFolderParameters.INSTALL_OUTPUT_FILES_PATH))) {
            FileOperationUtils.deleteFolderContent(L0uFolderParameters.INSTALL_OUTPUT_FILES_PATH);
        }

        FileOperationUtils.deleteFolderContent(L0uFolderParameters.WORKSPACE_PATH);
    }

    private void killProcesses() {
        PROCESSES.forEach(ProcessUtils::kill);
    }

}
