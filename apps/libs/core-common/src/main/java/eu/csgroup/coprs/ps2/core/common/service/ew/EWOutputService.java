package eu.csgroup.coprs.ps2.core.common.service.ew;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;

import java.util.Set;

public interface EWOutputService<T extends ExecutionInput> {

    Set<ProcessingMessage> output(T executionInput);

}
