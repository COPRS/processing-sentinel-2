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

package eu.csgroup.coprs.ps2.pw.l0u.service;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWProcessorService;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.model.SessionEntity;
import eu.csgroup.coprs.ps2.pw.l0u.service.output.L0uPWMessageService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.L0uPWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionManagementService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionService;
import eu.csgroup.coprs.ps2.pw.l0u.service.setup.L0uPWInputManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class L0uPWProcessorService extends PWProcessorService<L0uExecutionInput, Session, SessionEntity, SessionService> {

    public L0uPWProcessorService(
            L0uPWInputManagementService inputManagementService,
            SessionManagementService itemManagementService,
            L0uPWExecutionInputService executionInputService,
            L0uPWMessageService messageService
    ) {
        super(inputManagementService, itemManagementService, executionInputService, messageService);
    }

}
