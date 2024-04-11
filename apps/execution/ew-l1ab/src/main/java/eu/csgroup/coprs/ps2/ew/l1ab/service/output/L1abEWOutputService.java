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

package eu.csgroup.coprs.ps2.ew.l1ab.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWOutputService;
import eu.csgroup.coprs.ps2.ew.l1ab.service.setup.L1abEWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class L1abEWOutputService extends L1EWOutputService {

    private final L1abEWUploadService uploadService;

    public L1abEWOutputService(L1abEWMessageService messageService, L1abEWCleanupService cleanupService, L1abEWUploadService uploadService) {
        super(messageService, cleanupService);
        this.uploadService = uploadService;
    }

    @Override
    protected Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput, UUID parentUid) {
        return uploadService.upload(executionInput, parentUid);
    }

}
