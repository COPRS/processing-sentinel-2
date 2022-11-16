package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.config.CleanupProperties;
import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EWCleanupService<T extends ExecutionInput> {

    protected final CleanupProperties cleanupProperties;

    protected EWCleanupService(CleanupProperties cleanupProperties) {
        this.cleanupProperties = cleanupProperties;
    }

    public void cleanAndPrepare(String sharedFolderRoot) {

        log.info("Cleaning and setting up workspace");

        FileOperationUtils.deleteFolderContent(FolderParameters.WORKING_FOLDER_ROOT);

        if (cleanupProperties.isSharedEnabled()) {
            FileOperationUtils.deleteExpiredFolders(sharedFolderRoot, cleanupProperties.getHours());
        }

        doCleanBefore();
        doPrepare();

        log.info("Finished cleaning and setting up workspace");
    }

    public void clean(T executionInput) {

        log.info("Cleaning up workspace");

        if (cleanupProperties.isLocalEnabled()) {
            FileOperationUtils.deleteFolderContent(FolderParameters.WORKING_FOLDER_ROOT);
        }
        doCleanAfter(executionInput);

        log.info("Finished cleaning up workspace");
    }

    protected abstract void doPrepare();

    protected abstract void doCleanBefore();

    protected abstract void doCleanAfter(T executionInput);

}
