package eu.csgroup.coprs.ps2.core.common.service.ew;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;

public interface EWSetupService<T extends ExecutionInput> {

    void setup(T executionInput);

}
