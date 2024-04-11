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

package eu.csgroup.coprs.ps2.ew.l1sa.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1sabEWProcessorService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.exec.L1saEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.output.L1saEWOutputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWInputService;
import eu.csgroup.coprs.ps2.ew.l1sa.service.setup.L1saEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L1saEWProcessorService extends L1sabEWProcessorService {

    protected L1saEWProcessorService(
            L1saEWInputService inputService,
            L1saEWSetupService setupService,
            L1saEWExecutionService executionService,
            L1saEWOutputService outputService,
            MissingOutputProperties missingOutputProperties
    ) {
        super(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L1ExecutionInput executionInput) {
        return getL1sabMissingOutputs(executionInput);
    }

}
