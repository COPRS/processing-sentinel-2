package eu.csgroup.coprs.ps2.pw.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.FileType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.input.SingleFileInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.EmptyTaskOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.service.pw.PWInputManagementService;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.common.utils.SessionUtils;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.time.Instant;
import java.util.UUID;


@Slf4j
@Component
public class L0uPWInputManagementService implements PWInputManagementService {

    private final SessionManagementService managementService;

    public L0uPWInputManagementService(SessionManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public UUID manageInput(ProcessingMessage processingMessage) {

        FileType fileType = extract(processingMessage);
        String fileName = ObsUtils.keyToName(processingMessage.getKeyObjectStorage());

        log.info("Received message for {} file: {}", fileType.name(), fileName);

        TaskReport taskReport = new TaskReport()
                .setTaskName(ReportTask.PRODUCTION_TRIGGER.getName())
                .setSatellite(processingMessage.getMissionId() + processingMessage.getSatelliteId());

        taskReport.begin("Received CatalogEvent for product " + fileName, new SingleFileInput(fileName));

        try {

            final String product = "Product " + fileName;

            // Looking for t0 in metadata and additionalFields, since it's unclear where it's supposed to be - not required, anyway
            Instant t0PdgsDate = Instant.EPOCH;
            if (ProcessingMessageUtils.hasAdditionalField(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD)) {
                t0PdgsDate = ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD, Instant.class);
            } else if (ProcessingMessageUtils.hasMetadata(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD)) {
                t0PdgsDate = ProcessingMessageUtils.getMetadata(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD, Instant.class);
            }

            switch (fileType) {
                case DSIB -> {
                    managementService.create(SessionUtils.sessionFromFilename(fileName), t0PdgsDate);
                    taskReport.end(product + " is DSIB, creating session");
                }
                case DSDB -> {
                    managementService.updateRawComplete(SessionUtils.sessionFromFilename(fileName));
                    taskReport.end(product + " is RAW, updating sessions");
                }
                default -> taskReport.end(product + " is AUX, updating sessions");
            }

        } catch (Exception e) {
            taskReport.error("Error managing input for product " + fileName);
            throw e;
        }

        return taskReport.getUid();
    }

    private FileType extract(@Valid ProcessingMessage processingMessage) {

        final String filename = ObsUtils.keyToName(processingMessage.getKeyObjectStorage());

        return switch (processingMessage.getProductFamily()) {
            case EDRS_SESSION -> filename.endsWith(".raw") ? FileType.DSDB : FileType.DSIB;
            case S2_AUX -> FileType.AUX;
            default -> FileType.UNKNOWN;
        };
    }

}
