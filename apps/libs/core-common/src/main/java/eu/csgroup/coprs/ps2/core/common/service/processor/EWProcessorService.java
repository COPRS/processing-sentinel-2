package eu.csgroup.coprs.ps2.core.common.service.processor;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.input.JobProcessingInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.JobProcessingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWExecutionService;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWInputService;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWOutputService;
import eu.csgroup.coprs.ps2.core.common.service.ew.EWSetupService;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EWProcessorService<T extends ExecutionInput> extends ProcessorService {

    protected final EWInputService<T> inputService;
    protected final EWSetupService<T> setupService;
    protected final EWExecutionService<T> executionService;
    protected final EWOutputService<T> outputService;

    protected EWProcessorService(EWInputService<T> inputService, EWSetupService<T> setupService, EWExecutionService<T> executionService, EWOutputService<T> outputService) {
        this.inputService = inputService;
        this.setupService = setupService;
        this.executionService = executionService;
        this.outputService = outputService;
    }


    @Override
    protected Set<ProcessingMessage> processMessage(ProcessingMessage processingMessage) {

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.JOB_PROCESSING.getName())
                .setSatellite(processingMessage.getMissionId() + processingMessage.getSatelliteId());

        final T executionInput = inputService.extract(processingMessage);

        taskReport.begin("Start Job Processing", new JobProcessingInput(inputService.getTaskInputs(executionInput)));

        Set<ProcessingMessage> outputMessageSet;

        try {

            setupService.setup(executionInput);

            executionService.execute(executionInput, taskReport.getUid());

            outputMessageSet = outputService.output(executionInput);

            taskReport.end(
                    "End Job Processing",
                    new JobProcessingOutput(getTaskOutputs(outputMessageSet), executionInput.getT0PdgsDate()),
                    Collections.emptyList());

        } catch (Exception e) {
            taskReport.error(e.getLocalizedMessage(), Collections.emptyList());
            throw e;
        }

        return outputMessageSet;
    }

    protected Set<String> getTaskOutputs(Set<ProcessingMessage> processingMessages) {
        return processingMessages.stream()
                .filter(processingMessage -> StringUtils.hasText(processingMessage.getKeyObjectStorage()))
                .map(processingMessage -> ObsUtils.keyToName(processingMessage.getKeyObjectStorage()))
                .collect(Collectors.toSet());
    }

}
