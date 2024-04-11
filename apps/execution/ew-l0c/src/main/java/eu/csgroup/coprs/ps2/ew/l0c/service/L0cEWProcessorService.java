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

package eu.csgroup.coprs.ps2.ew.l0c.service;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l0c.service.exec.L0cEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l0c.service.output.L0cEWOutputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWInputService;
import eu.csgroup.coprs.ps2.ew.l0c.service.setup.L0cEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Configuration
public class L0cEWProcessorService extends EWProcessorService<L0cExecutionInput> {

    public L0cEWProcessorService(
            L0cEWInputService inputService,
            L0cEWSetupService setupService,
            L0cEWExecutionService executionService,
            L0cEWOutputService outputService,
            MissingOutputProperties missingOutputProperties
    ) {
        super(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L0cExecutionInput executionInput) {

        final JobProcessingTaskMissingOutput dsMissingOutput = buildMissingOutput(
                MissingOutputProductType.L0_DS, 1, executionInput.getSatellite(), 0, true, missingOutputProperties.getL0cIpfVersion()
        );

        int grCount;
        try {
            grCount = FileOperationUtils.findFoldersInTree(Paths.get(executionInput.getDtFolder()), S2FileParameters.L0U_GR_REGEX).size();
        } catch (FileOperationException e) {
            grCount = missingOutputProperties.getL0cDefaultGrCount();
        }

        final JobProcessingTaskMissingOutput grMissingOutput = buildMissingOutput(
                MissingOutputProductType.L0_GR, grCount, executionInput.getSatellite(), 0, true, missingOutputProperties.getL0cIpfVersion()
        );

        return List.of(dsMissingOutput, grMissingOutput);
    }

}
