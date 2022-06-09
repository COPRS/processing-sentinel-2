package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.ew.l0c.settings.L0cFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Set;

@Slf4j
@Service
public class CleanupService {

    public void cleanAndPrepare() {

        log.info("Cleaning and setting up workspace");

        FileOperationUtils.deleteFolderContent(L0cFolderParameters.WORKSPACE_PATH);
        FileOperationUtils.createFolders(L0cFolderParameters.WORKSPACE_FOLDERS);

        log.info("Finished cleaning and setting up workspace");
    }

    public void clean(L0cExecutionInput l0cExecutionInput) {

        log.info("Cleaning up workspace");

        FileOperationUtils.deleteFolderContent(L0cFolderParameters.WORKSPACE_PATH);
        FileOperationUtils.deleteFolders(Set.of(l0cExecutionInput.getDtFolder()));
        FileOperationUtils.deleteFolderIfEmpty(Paths.get(l0cExecutionInput.getDtFolder()).getParent().toString());

        log.info("Finished cleaning up workspace");
    }

}
