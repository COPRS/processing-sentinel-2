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

package eu.csgroup.coprs.ps2.pw.l0c.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripEntity;
import eu.csgroup.coprs.ps2.pw.l0c.service.output.L0cPWMessageService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cDatastripManagementService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cDatastripService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cPWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0c.service.setup.L0cPWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L0cPWProcessorService extends PWProcessorService<L0cExecutionInput, L0cDatastrip, L0cDatastripEntity, L0cDatastripService> {

    public L0cPWProcessorService(
            L0cPWInputManagementService inputManagementService,
            L0cDatastripManagementService managementService,
            L0cPWExecutionInputService executionInputService,
            L0cPWMessageService messageService
    ) {
        super(inputManagementService, managementService, executionInputService, messageService);
    }

}
