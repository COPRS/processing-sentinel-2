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
