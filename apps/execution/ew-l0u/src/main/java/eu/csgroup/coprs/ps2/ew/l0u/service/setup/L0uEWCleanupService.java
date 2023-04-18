package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

@Slf4j
@Component
public class L0uEWCleanupService extends EWCleanupService<L0uExecutionInput> {

    private static final Set<String> PROCESSES = Set.of(
            "EISPProcessor",
            "launch_eisp_ing_typ.bash",
            "launch_eisp.bash",
            "launch_merge.bash",
            "launch_telemetry.bash"
    );

    protected L0uEWCleanupService(CleanupProperties cleanupProperties) {
        super(cleanupProperties);
    }

    @Override
    protected void doPrepare() {
        FileOperationUtils.createFolders(L0uFolderParameters.WORKSPACE_FOLDERS);
    }

    @Override
    protected void doCleanBefore() {
        deleteFolders();
        killProcesses();
    }

    @Override
    public void doCleanAfter(L0uExecutionInput executionInput) {
        killProcesses();
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

    }

    private void killProcesses() {
        PROCESSES.forEach(ProcessUtils::kill);
    }

}
