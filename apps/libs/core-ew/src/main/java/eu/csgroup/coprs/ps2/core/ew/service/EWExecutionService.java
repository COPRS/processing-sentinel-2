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

package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;

import java.util.UUID;

public interface EWExecutionService<T extends ExecutionInput> {

    default void execute(T executionInput, UUID parentTaskUid) {

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PROCESSING.getName())
                .setSatellite(Mission.S2.getValue() + executionInput.getSatellite())
                .setParentUid(parentTaskUid);

        taskReport.begin("Start " + getLevel() + " processing");

        try {
            processing(executionInput, taskReport.getUid());
        } catch (Exception e) {
            taskReport.error(e.getLocalizedMessage());
            throw e;
        }

        taskReport.end("End " + getLevel() + " processing");
    }

    void processing(T executionInput, UUID parentTaskUid);

    String getLevel();

}
