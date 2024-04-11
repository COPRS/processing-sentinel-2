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

package eu.csgroup.coprs.ps2.core.ew.service.l1;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;

public abstract class L1EWProcessorService extends EWProcessorService<L1ExecutionInput> {

    protected L1EWProcessorService(L1EWInputService inputService,
            EWSetupService<L1ExecutionInput> setupService,
            L01EWExecutionService<L1ExecutionInput> executionService,
            L1EWOutputService outputService,
            MissingOutputProperties missingOutputProperties
    ) {
        super(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    protected JobProcessingTaskMissingOutput buildL1MissingOutput(MissingOutputProductType type, Integer count, L1ExecutionInput executionInput) {
        return buildMissingOutput(type, count, executionInput.getSatellite(), 1, true, missingOutputProperties.getL1IpfVersion());
    }


}
