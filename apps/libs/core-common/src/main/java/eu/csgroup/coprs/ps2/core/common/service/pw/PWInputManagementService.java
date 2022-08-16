package eu.csgroup.coprs.ps2.core.common.service.pw;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;

import java.util.UUID;

public interface PWInputManagementService {

    UUID manageInput(ProcessingMessage processingMessage);

}
