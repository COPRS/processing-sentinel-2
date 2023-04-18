package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;

import java.util.UUID;

public interface PWInputManagementService {

    UUID manageInput(ProcessingMessage processingMessage);

}
