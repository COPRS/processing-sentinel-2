package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1cPWInputManagementService {

    public L1ExecutionInput manageInput(ProcessingMessage processingMessage) {

        log.info("Received message {}", processingMessage);

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PRODUCTION_TRIGGER.getName())
                .setSatellite(processingMessage.getMissionId() + processingMessage.getSatelliteId());

        taskReport.begin("Received L1 Execution message");

        L1ExecutionInput executionInput;

        try {
            executionInput = extract(processingMessage);
            taskReport.end("Successfully extracted processing input");
        } catch (Exception e) {
            taskReport.error("Error managing input for message " + processingMessage);
            throw e;
        }

        return executionInput;
    }

    private L1ExecutionInput extract(ProcessingMessage processingMessage) {
        log.info("Extracting input from message");
        final L1ExecutionInput l1ExecutionInput = ProcessingMessageUtils.getAdditionalField(
                processingMessage,
                MessageParameters.EXECUTION_INPUT_FIELD,
                L1ExecutionInput.class
        );
        log.info("Finished extracting input from message");
        return l1ExecutionInput;
    }

}
