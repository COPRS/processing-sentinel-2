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

package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class L0uEWSetupService implements EWSetupService<L0uExecutionInput> {

    private final L0uEWCleanupService cleanupService;
    private final L0uEWJobOrderService jobOrderService;
    private final L0uEWDownloadService downloadService;
    private final SharedProperties sharedProperties;

    public L0uEWSetupService(L0uEWCleanupService cleanupService, L0uEWJobOrderService jobOrderService, L0uEWDownloadService downloadService,
            SharedProperties sharedProperties
    ) {
        this.cleanupService = cleanupService;
        this.jobOrderService = jobOrderService;
        this.downloadService = downloadService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void setup(L0uExecutionInput l0uExecutionInput, UUID parentUid) {

        log.info("Starting setup ...");

        cleanupService.cleanAndPrepare(sharedProperties.getSharedFolderRoot());
        jobOrderService.saveJobOrders(l0uExecutionInput);
        downloadService.download(l0uExecutionInput.getFiles(), parentUid);

        log.info("Finished setup.");
    }

}
