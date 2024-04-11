/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    protected void doPrepare() {
        // By default, nothing to do
    }

    protected void doCleanBefore() {
        // By default, nothing to do
    }

    protected void doCleanAfter(T executionInput) {
        // By default, nothing to do
    }

}
