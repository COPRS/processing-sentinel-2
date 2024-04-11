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

package eu.csgroup.coprs.ps2.pw.l2.service;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripEntity;
import eu.csgroup.coprs.ps2.pw.l2.service.output.L2PWMessageService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2PWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l2.service.setup.L2PWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L2PWProcessorService extends PWProcessorService<L2ExecutionInput, L2Datastrip, L2DatastripEntity, L2DatastripService> {

    public L2PWProcessorService(
            L2PWInputManagementService inputManagementService,
            L2DatastripManagementService itemManagementService,
            L2PWExecutionInputService executionInputService,
            L2PWMessageService messageService
    ) {
        super(inputManagementService, itemManagementService, executionInputService, messageService);
    }

}
