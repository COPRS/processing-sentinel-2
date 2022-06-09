package eu.csgroup.coprs.ps2.ew.l0c.service.exec;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.exception.ProcessingException;
import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cJobOrderFields;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import eu.csgroup.coprs.ps2.ew.l0c.settings.EWL0cTask;
import eu.csgroup.coprs.ps2.ew.l0c.settings.L0cFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ExecutionService {

    private static final List<EWL0cTask> PART_1_TASKS = List.of(
            EWL0cTask.GSE,
            EWL0cTask.INIT_L0C_L0,
            EWL0cTask.FORMAT_ISP
    );
    private static final List<EWL0cTask> PART_2_TASKS = List.of(
            EWL0cTask.QL_GEO,
            EWL0cTask.QL_CLOUD_MASK,
            EWL0cTask.FORMAT_METADATA_GR_L0C,
            EWL0cTask.FORMAT_IMG_QL_L0,
            EWL0cTask.OLQC_L0CGR,
            EWL0cTask.FORMAT_METADATA_DS_L0C,
            EWL0cTask.OLQC_L0CDS
    );
    private static final String INDEX_PLACEHOLDER = "XX";

    public void execute(L0cExecutionInput l0cExecutionInput) {

        log.info("Starting L0C processing");

        Map<String, String> values = new HashMap<>();

        getApplicableTasks(PART_1_TASKS, l0cExecutionInput).forEach(task -> runTask(task, values));

        values.put(L0cJobOrderFields.L0_DS_NAME.getPlaceholder(), getL0DSName());

        for (int i = 0; i < JobParameters.BAND_COUNT; i++) {
            final String index = String.format("%02d", i + 1);
            final List<String> l0GRList = getL0GRList(index);
            values.put(L0cJobOrderFields.L0_GR_COUNT.getPlaceholder().replace(INDEX_PLACEHOLDER, index), String.valueOf(l0GRList.size()));
            values.put(L0cJobOrderFields.L0_GR_LIST.getPlaceholder().replace(INDEX_PLACEHOLDER, index), buildXmlValue(l0GRList));
        }

        getApplicableTasks(PART_2_TASKS, l0cExecutionInput).forEach(task -> runTask(task, values));

        log.info("Finished L0C processing");
    }

    private void runTask(EWL0cTask task, Map<String, String> values) {

        final String taskName = task.getL0CTask().getTaskName();

        log.info("Running task {}", taskName);

        Path jobOrdersFolderPath = Paths.get(L0cFolderParameters.JOB_ORDERS_PATH, task.getL0CTask().name());

        Set<Path> jobOrderSet = new HashSet<>();

        try (final Stream<Path> jobOrders = Files.list(jobOrdersFolderPath)) {

            jobOrders.forEach(jobOrder -> {
                if (values.size() != 0) {
                    log.debug("Replacing values in Job Order {}", jobOrder);
                    FileContentUtils.replaceInFile(jobOrder, values);
                }
                jobOrderSet.add(jobOrder);
            });

        } catch (IOException e) {
            throw new FileOperationException("Unable to access Job Orders for task " + task.name(), e);
        }

        runJobOrdersForTask(taskName, task.getScript(), jobOrderSet);

        log.info("Finished running task {}", taskName);
    }

    private void runJobOrdersForTask(String task, String script, Set<Path> jobOrderSet) {

        log.info("Executing {} Job Order(s) for task {}", jobOrderSet.size(), task);

        final Set<ScriptWrapper> scriptWrapperSet = jobOrderSet.stream()
                .map(path -> new ScriptWrapper()
                        .setRunId(path.getFileName().toString())
                        .setWorkdir(L0cFolderParameters.WORKSPACE_PATH)
                        .setCommand(List.of(script, path.toString())))
                .collect(Collectors.toSet());

        ScriptUtils.run(scriptWrapperSet)
                .forEach((id, exitCode) -> {
                    if (exitCode != 0) {
                        throw new ScriptExecutionException("Error while executing Job Order " + id);
                    }
                });

        log.info("Finished executing Job Order(s) for task {}", task);
    }

    private String getL0DSName() {
        return FileOperationUtils.findFolders(Paths.get(L0cFolderParameters.DS_PATH), S2FileParameters.L0C_DS_REGEX)
                .stream()
                .findAny()
                .orElseThrow(() -> new ProcessingException("Unable to find L0 DS"))
                .toString();
    }

    private List<String> getL0GRList(String index) {
        return FileOperationUtils.findFolders(Paths.get(L0cFolderParameters.GR_DB_PATH), S2FileParameters.L0C_GR_REGEX_TEMPLATE.replace(INDEX_PLACEHOLDER, index))
                .stream()
                .map(Path::toString)
                .toList();
    }

    private String buildXmlValue(List<String> fileList) {
        return fileList.stream().map(s -> "<File_Name>" + s + "</File_Name>").collect(Collectors.joining("\n"));
    }

    public static List<EWL0cTask> getApplicableTasks(List<EWL0cTask> taskList, L0cExecutionInput l0cExecutionInput) {
        return taskList.stream().filter(task -> task.getL0CTask().getSatellites().contains(l0cExecutionInput.getSatellite())).toList();
    }

}
