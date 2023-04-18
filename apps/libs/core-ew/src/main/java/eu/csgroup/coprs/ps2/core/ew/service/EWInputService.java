package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;

import java.util.Set;

public interface EWInputService<T extends ExecutionInput> {

    T extract(ProcessingMessage processingMessage);

    Set<String> getTaskInputs(T executionInput);

}
