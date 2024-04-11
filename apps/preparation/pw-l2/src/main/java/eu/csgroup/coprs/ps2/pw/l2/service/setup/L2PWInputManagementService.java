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

package eu.csgroup.coprs.ps2.pw.l2.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.input.SingleFileInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.pw.service.PWInputManagementService;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@Component
public class L2PWInputManagementService implements PWInputManagementService {

    private final L2DatastripManagementService managementService;

    public L2PWInputManagementService(L2DatastripManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public UUID manageInput(ProcessingMessage processingMessage) {


        final ProductFamily productFamily = processingMessage.getProductFamily();
        String fileName = ObsUtils.keyToName(processingMessage.getKeyObjectStorage());

        log.info("Received message for {} product: {}", productFamily.name(), fileName);

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PRODUCTION_TRIGGER.getName())
                .setSatellite(processingMessage.getMissionId() + processingMessage.getSatelliteId());

        taskReport.begin("Received CatalogEvent for product " + fileName, new SingleFileInput(fileName));

        try {

            final String product = "Product " + fileName;

            switch (productFamily) {
                case S2_L1C_DS -> {
                    managementService.create(
                            fileName,
                            processingMessage.getSatelliteId(),
                            ProcessingMessageUtils.getT0PdgsDate(processingMessage),
                            processingMessage.getStoragePath()
                    );
                    taskReport.end(product + " is DS, creating datastrip");
                }
                case S2_L1C_TL -> {
                    final String datastripName = ProcessingMessageUtils.getMetadata(
                            processingMessage,
                            MessageParameters.DATASTRIP_ID_FIELD,
                            String.class
                    );
                    managementService.updateTLComplete(datastripName);
                    taskReport.end(product + " is TL, updating datastrip");
                }
                default -> taskReport.end(product + " is AUX, updating datastrips");
            }

        } catch (Exception e) {
            taskReport.error("Error managing input for product " + fileName);
            throw e;
        }

        return taskReport.getUid();
    }

}
