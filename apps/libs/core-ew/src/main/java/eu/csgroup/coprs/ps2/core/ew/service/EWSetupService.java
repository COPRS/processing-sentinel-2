package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;

import java.util.UUID;

public interface EWSetupService<T extends ExecutionInput> {

    void setup(T executionInput, UUID parentUid);

}
