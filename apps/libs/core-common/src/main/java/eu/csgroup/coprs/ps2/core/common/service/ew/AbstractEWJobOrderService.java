package eu.csgroup.coprs.ps2.core.common.service.ew;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.l0.ExecutionInput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractEWJobOrderService<T extends ExecutionInput> {

    public abstract void saveJobOrders(T executionInput);

    protected void save(Map<String, String> jobOrderByName, Path folderPath) {
        jobOrderByName.forEach((name, jobOrder) -> {
            try {
                Files.writeString(folderPath.resolve(name), jobOrder);
            } catch (IOException e) {
                throw new FileOperationException("Unable to save Job Order", e);
            }
        });
    }

}
