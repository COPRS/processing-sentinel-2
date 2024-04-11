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

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
public abstract class EWOutputService<T extends ExecutionInput> {

    protected final EWMessageService<T> messageService;
    protected final EWCleanupService<T> cleanupService;

    protected EWOutputService(EWMessageService<T> messageService, EWCleanupService<T> cleanupService) {
        this.messageService = messageService;
        this.cleanupService = cleanupService;
    }

    public Set<ProcessingMessage> output(T executionInput, UUID parentUid) {

        log.info("Starting post execution tasks");

        final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = upload(executionInput, parentUid);

        final String outputFolder = copy();

        final Set<ProcessingMessage> messages = messageService.build(executionInput, fileInfoByFamily, outputFolder);

        cleanupService.clean(executionInput);

        log.info("Finished post execution tasks");

        return messages;
    }

    protected abstract Map<ProductFamily, Set<FileInfo>> upload(T executionInput, UUID parentUid);

    protected String copy() {
        // By default, noting to do
        return null;
    }

}
