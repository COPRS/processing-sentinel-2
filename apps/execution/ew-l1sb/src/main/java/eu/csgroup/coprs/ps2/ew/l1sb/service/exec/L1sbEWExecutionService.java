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

package eu.csgroup.coprs.ps2.ew.l1sb.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l01.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;
import eu.csgroup.coprs.ps2.core.ew.settings.L1EWParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class L1sbEWExecutionService extends L01EWExecutionService<L1ExecutionInput> {

    public L1sbEWExecutionService(SharedProperties sharedProperties) {
        super(sharedProperties);
    }

    @Override
    public void processing(L1ExecutionInput executionInput, UUID parentTaskUid) {

        log.info("Starting L1S processing");

        // Mode depends on whether we need the Grid files or not
        Path gridFileListPath = Paths.get(executionInput.getOutputFolder(), L1EWParameters.GRI_FILE_LIST_PATH);
        OrchestratorMode nextMode = FileContentUtils.grepAll(gridFileListPath, "<GRI_").isEmpty() ? OrchestratorMode.L1B_NO_GRI : OrchestratorMode.L1B;
        runMode(executionInput, parentTaskUid, nextMode);

        log.info("Finished L1s processing");
    }

    @Override
    public String getLevel() {
        return "L1sb";
    }

}
