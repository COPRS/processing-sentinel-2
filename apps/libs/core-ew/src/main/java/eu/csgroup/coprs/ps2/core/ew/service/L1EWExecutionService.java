package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import eu.csgroup.coprs.ps2.core.ew.config.L1ExecutionProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.ew.settings.L1EWParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class L1EWExecutionService<T extends ExecutionInput> implements EWExecutionService<T> {

    protected final L1ExecutionProperties executionProperties;

    protected L1EWExecutionService(L1ExecutionProperties executionProperties) {
        this.executionProperties = executionProperties;
    }

    protected void runMode(L1ExecutionInput executionInput, UUID parentTaskUid, OrchestratorMode mode, L1ExecutionProperties executionProperties) {

        final String task = mode.getMode();

        log.info("Running task " + task);

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PROCESSING_TASK.getName())
                .setSatellite(Mission.S2.name() + executionInput.getSatellite())
                .setParentUid(parentTaskUid);

        taskReport.begin("Start task: " + task);

        try {

            List<String> command = new java.util.ArrayList<>(List.of(
                    L1EWParameters.SCRIPT_NAME,
                    "-m", task,
                    "-a", executionInput.getAuxFolder(),
                    "-s", executionProperties.getDemFolderRoot(),
                    "-g", executionProperties.getGridFolderRoot(),
                    "-i", executionInput.getInputFolder(),
                    "-w", L1Parameters.WORKING_FOLDER_ROOT,
                    "-o", executionInput.getOutputFolder(),
                    "-p", String.valueOf(executionProperties.getMaxParallelTasks()),
                    "--exeversionfile", L1EWParameters.VERSION_FILE
            ));

            if (StringUtils.hasText(executionInput.getTile())) {
                command.add("-t");
                command.add(executionInput.getTile());
            }

            final Integer exitCode = ScriptUtils.run(
                    new ScriptWrapper()
                            .setRunId(executionInput.getDatastrip())
                            .setWorkdir(L1Parameters.WORKING_FOLDER_ROOT)
                            .setCommand(command)
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

}
