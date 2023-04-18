package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.service.processor.ProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Configuration
public class L1cPWProcessorService extends ProcessorService {

    private final L1cPWInputManagementService inputManagementService;
    private final L1cPWTileManagementService tileManagementService;
    private final L1cPWMessageService messageService;

    public L1cPWProcessorService(L1cPWInputManagementService inputManagementService, L1cPWTileManagementService tileManagementService, L1cPWMessageService messageService) {
        this.inputManagementService = inputManagementService;
        this.tileManagementService = tileManagementService;
        this.messageService = messageService;
    }

    @Override
    protected Set<ProcessingMessage> processMessage(ProcessingMessage processingMessage) {

        final L1ExecutionInput executionInput = inputManagementService.manageInput(processingMessage);

        final Set<String> tileSet = tileManagementService.listTiles(executionInput);

        List<TaskReport> taskReportList = tileSet.stream()
                .map(item -> {
                    TaskReport taskReport = new TaskReport()
                            .setTaskName(ReportTask.JOB_GENERATOR.getName())
                            .setSatellite(executionInput.getSatellite());
                    taskReport.begin("Start Job Generation");
                    return taskReport;
                })
                .toList();

        final Set<ProcessingMessage> outputMessages = messageService.build(executionInput, tileSet);

        IntStream.range(0, taskReportList.size())
                .forEach(value -> taskReportList.get(value).end("End Job Generation"));

        return outputMessages;
    }

}
