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

import eu.csgroup.coprs.ps2.core.common.model.CommonInput;
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
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EWProcessorService<T extends ExecutionInput> extends ProcessorService {

    protected final EWInputService<T> inputService;
    protected final EWSetupService<T> setupService;
    protected final EWExecutionService<T> executionService;
    protected final EWOutputService<T> outputService;
    protected final MissingOutputProperties missingOutputProperties;

    protected EWProcessorService(EWInputService<T> inputService, EWSetupService<T> setupService, EWExecutionService<T> executionService, EWOutputService<T> outputService, MissingOutputProperties missingOutputProperties) {
        this.inputService = inputService;
        this.setupService = setupService;
        this.executionService = executionService;
        this.outputService = outputService;
        this.missingOutputProperties = missingOutputProperties;
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

            setupService.setup(executionInput, taskReport.getUid());

            executionService.execute(executionInput, taskReport.getUid());

            outputMessageSet = outputService.output(executionInput, taskReport.getUid());

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

    protected Set<String> getTaskOutputs(Set<ProcessingMessage> outputMessages) {

        // Adding entries for products going into the catalog
        final Set<String> taskOutputs = outputMessages.stream()
                .filter(processingMessage -> StringUtils.hasText(processingMessage.getKeyObjectStorage()))
                .map(processingMessage -> ObsUtils.keyToName(processingMessage.getKeyObjectStorage()))
                .collect(Collectors.toSet());

        // Adding products destined for next processing step
        final Set<String> customOutputs = outputMessages.stream()
                .filter(processingMessage -> !StringUtils.hasText(processingMessage.getKeyObjectStorage()))
                .map(processingMessage -> {
                    Set<String> customInputs = Collections.emptySet();
                    final CommonInput commonInput = ProcessingMessageUtils.getCommonInput(processingMessage);
                    if (commonInput != null) {
                        customInputs = commonInput.getCustomTaskInputs();
                    }
                    return customInputs;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        taskOutputs.addAll(customOutputs);

        return taskOutputs;
    }

    protected JobProcessingTaskMissingOutput buildMissingOutput(MissingOutputProductType type, Integer count, String satellite, int level, Boolean endToEnd, String ipf) {
        return new JobProcessingTaskMissingOutput()
                .setEndToEndProductBoolean(endToEnd)
                .setEstimatedCountInteger(count)
                .setProductMetadataCustomObject(
                        new ProductMetadata()
                                .setProductTypeString(type.getType())
                                .setPlatformSerialIdentifierString(satellite)
                                .setProcessingLevelInteger(level)
                                .setProductConsolidatedBoolean(endToEnd)
                                .setProcessorVersionString(ipf)
                );
    }

}
