package eu.csgroup.coprs.ps2.core.ew.service.l01;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import eu.csgroup.coprs.ps2.core.common.model.L012ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l01.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWExecutionService;
import eu.csgroup.coprs.ps2.core.ew.settings.L1EWParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class L01EWExecutionService<T extends L012ExecutionInput> implements EWExecutionService<T> {

    protected final SharedProperties sharedProperties;

    protected L01EWExecutionService(SharedProperties sharedProperties) {
        this.sharedProperties = sharedProperties;
    }

    protected void runMode(T executionInput, UUID parentTaskUid, OrchestratorMode mode) {

        final String task = mode.getMode();

        log.info("Running task " + task);

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PROCESSING_TASK.getName())
                .setSatellite(Mission.S2.name() + executionInput.getSatellite())
                .setParentUid(parentTaskUid);

        taskReport.begin("Start task: " + task);

        try {

            List<String> command = new ArrayList<>(List.of(
                    L1EWParameters.SCRIPT_NAME,
                    "-m", task,
                    "-a", executionInput.getAuxFolder(),
                    "-s", sharedProperties.getDemFolderRoot(),
                    "-g", sharedProperties.getGridFolderRoot(),
                    "-i", executionInput.getInputFolder(),
                    "-w", FolderParameters.WORKING_FOLDER_ROOT,
                    "-o", executionInput.getOutputFolder(),
                    "-p", String.valueOf(sharedProperties.getMaxParallelTasks()),
                    "--exeversionfile", L1EWParameters.VERSION_FILE,
                    "-k", String.valueOf(sharedProperties.getKillTimeout())
            ));

            if (StringUtils.hasText(executionInput.getTile())) {
                command.add("-t");
                command.add(executionInput.getTile());
            }

            final Integer exitCode = ScriptUtils.run(
                    new ScriptWrapper()
                            .setRunId(executionInput.getDatastrip())
                            .setWorkdir(FolderParameters.WORKING_FOLDER_ROOT)
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
