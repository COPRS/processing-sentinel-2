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

package eu.csgroup.coprs.ps2.core.ew.service.l2;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWExecutionService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class L2EWExecutionService implements EWExecutionService<L2ExecutionInput> {

    protected final SharedProperties sharedProperties;

    protected L2EWExecutionService(SharedProperties sharedProperties) {
        this.sharedProperties = sharedProperties;
    }

    @Override
    public void processing(L2ExecutionInput executionInput, UUID parentTaskUid) {

        final String task = getLevel();

        log.info("Running task " + task);

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PROCESSING_TASK.getName())
                .setSatellite(Mission.S2.name() + executionInput.getSatellite())
                .setParentUid(parentTaskUid);

        taskReport.begin("Start task: " + task);

        try {

            final Integer exitCode = ScriptUtils.run(
                    new ScriptWrapper()
                            .setRunId(executionInput.getDatastrip())
                            .setWorkdir(FolderParameters.WORKING_FOLDER_ROOT)
                            .setCommand(getCommand(executionInput))
            );

            if (exitCode != 0) {
                throw new ScriptExecutionException("Error during task " + task + " - Exit code: " + exitCode);
            }

            taskReport.end("End task " + task + " with exit code " + exitCode);

        } catch (Exception e) {
            taskReport.error(e.getLocalizedMessage());
            throw e;
        }

        log.info("Finished task " + task);
    }

    protected abstract List<String> getCommand(L2ExecutionInput executionInput);

}
