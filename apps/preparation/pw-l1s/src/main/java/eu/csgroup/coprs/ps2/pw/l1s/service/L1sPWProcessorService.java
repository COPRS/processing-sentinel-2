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

package eu.csgroup.coprs.ps2.pw.l1s.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripEntity;
import eu.csgroup.coprs.ps2.pw.l1s.service.output.L1sPWMessageService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sDatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sDatastripService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sPWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l1s.service.setup.L1sPWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L1sPWProcessorService extends PWProcessorService<L1ExecutionInput, L1sDatastrip, L1sDatastripEntity, L1sDatastripService> {

    public L1sPWProcessorService(
            L1sPWInputManagementService inputManagementService,
            L1sDatastripManagementService itemManagementService,
            L1sPWExecutionInputService executionInputService,
            L1sPWMessageService messageService
    ) {
        super(inputManagementService, itemManagementService, executionInputService, messageService);
    }

    @Override
    protected String noProcessingMessage() {
        return "minGrRequired";
    }

}
