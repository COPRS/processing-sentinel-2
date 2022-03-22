package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.execution.L0uExecutionInput;
import eu.csgroup.coprs.ps2.ew.l0u.settings.FolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Slf4j
@Component
public class JobOrderService {

    public void saveJobOrders(L0uExecutionInput l0uExecutionInput) {

        log.info("Saving job orders to files");

        l0uExecutionInput.getJobOrders().forEach((key, value) -> {
            try {
                Files.writeString(Paths.get(FolderParameters.WORKSPACE_PATH, key), value);
            } catch (IOException e) {
                throw new FileOperationException("Unable to save Job Order", e);
            }
        });

        log.info("Job orders saved");
    }

}
