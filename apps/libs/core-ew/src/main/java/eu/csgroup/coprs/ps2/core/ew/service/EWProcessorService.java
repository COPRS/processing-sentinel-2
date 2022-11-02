package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.input.JobProcessingInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.JobProcessingTaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.ProductMetadata;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.JobProcessingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.service.processor.ProcessorService;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
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

    protected abstract List<TaskMissingOutput> getMissingOutputs(T executionInput);

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
            taskReport.error(e.getLocalizedMessage(), getMissingOutputs(executionInput));
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

    protected JobProcessingTaskMissingOutput buildMissingOutput(MissingOutputProductType type, Integer count, String satellite, Boolean endToEnd, String ipf) {
        return new JobProcessingTaskMissingOutput()
                .setEndToEndProductBoolean(endToEnd)
                .setEstimatedCountInteger(count)
                .setProductMetadataCustomObject(
                        new ProductMetadata()
                                .setProductTypeString(type.getType())
                                .setPlatformSerialIdentifierString(satellite)
                                .setProcessingLevelInteger(0)
                                .setProductConsolidatedBoolean(true)
                                .setProductConsolidatedBoolean(endToEnd)
                                .setProcessorVersionString(ipf)
                );
    }

}
