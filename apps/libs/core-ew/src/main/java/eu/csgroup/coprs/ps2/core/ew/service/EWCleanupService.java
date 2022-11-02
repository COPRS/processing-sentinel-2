package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;

public interface EWCleanupService<T extends ExecutionInput> {

    void cleanAndPrepare();

    void clean(T executionInput);

}
