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

package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWOutputService;
import eu.csgroup.coprs.ps2.ew.l0u.service.setup.L0uEWCleanupService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;


@Service
public class L0uEWOutputService extends EWOutputService<L0uExecutionInput> {

    private final L0uEWUploadService uploadService;
    private final L0uEWCopyService copyService;


    public L0uEWOutputService(L0uEWMessageService messageService, L0uEWCleanupService cleanupService, L0uEWUploadService uploadService, L0uEWCopyService copyService) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
        this.copyService = copyService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L0uExecutionInput executionInput, UUID parentUid) {
        return uploadService.upload(executionInput, parentUid);
    }

    @Override
    protected String copy() {
        return copyService.copy();
    }

}
