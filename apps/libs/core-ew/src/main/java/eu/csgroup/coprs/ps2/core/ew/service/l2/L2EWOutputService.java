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

package eu.csgroup.coprs.ps2.core.ew.service.l2;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class L2EWOutputService extends EWOutputService<L2ExecutionInput> {

    protected final EWUploadService<L2ExecutionInput> uploadService;

    protected L2EWOutputService(EWMessageService<L2ExecutionInput> messageService, EWCleanupService<L2ExecutionInput> cleanupService,
            EWUploadService<L2ExecutionInput> uploadService
    ) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L2ExecutionInput executionInput, UUID parentUid) {
        return uploadService.upload(executionInput, parentUid);
    }

}
