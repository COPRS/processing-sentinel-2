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

package eu.csgroup.coprs.ps2.ew.l1c.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l01.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class L1cEWExecutionService extends L01EWExecutionService<L1ExecutionInput> {

    private static final List<OrchestratorMode> DS_TASKS = List.of(OrchestratorMode.OLQC_L1CDS);
    private static final List<OrchestratorMode> TL_TASKS = List.of(OrchestratorMode.L1C_TILE);

    protected L1cEWExecutionService(SharedProperties sharedProperties) {
        super(sharedProperties);
    }

    @Override
    public void processing(L1ExecutionInput executionInput, UUID parentTaskUid) {

        log.info("Starting L1c processing");

        List<OrchestratorMode> tasks = StringUtils.hasText(executionInput.getTile()) ? TL_TASKS : DS_TASKS;

        tasks.forEach(orchestratorMode -> runMode(executionInput, parentTaskUid, orchestratorMode));

        log.info("Finished L1c processing");
    }

    @Override
    public String getLevel() {
        return "L1c";
    }

}
