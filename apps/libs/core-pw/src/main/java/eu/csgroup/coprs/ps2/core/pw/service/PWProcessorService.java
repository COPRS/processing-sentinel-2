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

package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.service.processor.ProcessorService;
import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import eu.csgroup.coprs.ps2.core.pw.model.PWItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
public abstract class PWProcessorService<T extends ExecutionInput, S extends PWItem, I extends PWItemEntity, V extends PWItemService<S, I>> extends ProcessorService {

    protected final PWInputManagementService inputManagementService;
    protected final PWItemManagementService<S, I, V> itemManagementService;
    protected final PWExecutionInputService<T, S> executionInputService;
    protected final PWMessageService<T> messageService;

    protected PWProcessorService(PWInputManagementService inputManagementService, PWItemManagementService<S, I, V> itemManagementService,
            PWExecutionInputService<T, S> executionInputService, PWMessageService<T> messageService
    ) {
        this.inputManagementService = inputManagementService;
        this.itemManagementService = itemManagementService;
        this.executionInputService = executionInputService;
        this.messageService = messageService;
    }

    @Override
    protected Set<ProcessingMessage> processMessage(ProcessingMessage processingMessage) {
        cleanup();
        inputManagementService.manageInput(processingMessage);
        return manageItems();
    }

    protected void cleanup() {

        log.info("Cleaning up items ...");

        itemManagementService.cleanup();

        log.info("Finished cleaning up items ...");
    }

    protected Set<ProcessingMessage> manageItems() {

        Set<ProcessingMessage> outputMessageSet = new HashSet<>();

        log.info("Managing items ...");

        itemManagementService.updateAvailableAux();
        itemManagementService.updateNotReady();

        log.info("Fetching ready items ...");

        final List<S> readyItems = itemManagementService.getReady();

        log.info("Found {} ready items", readyItems.size());

        if (!CollectionUtils.isEmpty(readyItems)) {

            List<TaskReport> taskReportList = readyItems.stream()
                    .map(item -> {
                        TaskReport taskReport = new TaskReport()
                                .setTaskName(ReportTask.JOB_GENERATOR.getName())
                                .setSatellite(item.getSatelliteName());
                        taskReport.begin("Start Job Generation");
                        return taskReport;
                    })
                    .toList();

            List<T> executionInputList = executionInputService.create(readyItems);

            IntStream.range(0, executionInputList.size())
                    .forEach(value -> taskReportList.get(value).end("End Job Generation"));

            if (executionInputList.size() != readyItems.size()) {
                IntStream.range(executionInputList.size(), readyItems.size())
                        .forEach(value -> taskReportList.get(value).end("End Job Generation", Map.of("processing_conditions_not_met_string", noProcessingMessage())));
            }

            outputMessageSet = messageService.build(executionInputList);
            itemManagementService.setJobOrderCreated(readyItems);
        }

        return outputMessageSet;
    }

    protected String noProcessingMessage() {
        return "";
    }

}
