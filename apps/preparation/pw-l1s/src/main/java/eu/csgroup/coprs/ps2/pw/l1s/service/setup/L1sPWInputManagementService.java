package eu.csgroup.coprs.ps2.pw.l1s.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.input.SingleFileInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.pw.model.ResubmitMessage;
import eu.csgroup.coprs.ps2.core.pw.service.PWInputManagementService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sDatastripManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@Component
public class L1sPWInputManagementService implements PWInputManagementService {

    private final L1sDatastripManagementService managementService;

    public L1sPWInputManagementService(L1sDatastripManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public UUID manageInput(ProcessingMessage processingMessage) {


        final ProductFamily productFamily = processingMessage.getProductFamily();
        String fileName = ObsUtils.keyToName(processingMessage.getKeyObjectStorage());

        log.info("Received message for {} file: {}", productFamily.name(), fileName);

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PRODUCTION_TRIGGER.getName())
                .setSatellite(processingMessage.getMissionId() + processingMessage.getSatelliteId());

        taskReport.begin("Received CatalogEvent for product " + fileName, new SingleFileInput(fileName));

        try {

            final String product = "Product " + fileName;

            switch (productFamily) {
                case S2_L0_DS -> {
                    final ResubmitMessage resubmitMessage = createResubmitMessage(processingMessage);
                    managementService.create(
                            fileName,
                            processingMessage.getSatelliteId(),
                            ProcessingMessageUtils.getT0PdgsDate(processingMessage),
                            processingMessage.getStoragePath(), resubmitMessage
                    );
                    taskReport.end(product + " is DS, creating datastrip");
                }
                case S2_L0_GR -> {
                    final String datastripName = ProcessingMessageUtils.getMetadata(
                            processingMessage,
                            MessageParameters.DATASTRIP_ID_FIELD,
                            String.class
                    );
                    managementService.updateGRComplete(datastripName);
                    taskReport.end(product + " is GR, updating datastrip");
                }
                default -> taskReport.end(product + " is AUX, updating datastrips");
            }

        } catch (Exception e) {
            taskReport.error("Error managing input for product " + fileName);
            throw e;
        }

        return taskReport.getUid();
    }

    private ResubmitMessage createResubmitMessage(ProcessingMessage processingMessage) {
        ResubmitMessage message = new ResubmitMessage();
        message.setProductFamily(ProductFamily.S2_L0_DS)
                .setKeyObjectStorage(processingMessage.getKeyObjectStorage())
                .setMissionId(processingMessage.getMissionId())
                .setSatelliteId(processingMessage.getSatelliteId())
                .setStoragePath(processingMessage.getStoragePath())
                .setT0PdgsDate(ProcessingMessageUtils.getT0PdgsDate(processingMessage));

        return message;
    }

}
