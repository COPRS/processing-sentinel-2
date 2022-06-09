package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.service.ew.AbstractEWJobOrderService;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.ew.l0c.settings.L0cFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Set;

@Slf4j
@Service
public class EWJobOrderService extends AbstractEWJobOrderService<L0cExecutionInput> {

    public void saveJobOrders(L0cExecutionInput l0cExecutionInput) {

        log.info("Saving job orders to files");

        l0cExecutionInput.getJobOrders().forEach((task, jobOrders) -> {
            String taskFolder = L0cFolderParameters.JOB_ORDERS_PATH + "/" + task;
            FileOperationUtils.createFolders(Set.of(taskFolder));
            save(jobOrders, Paths.get(taskFolder));
        });

        log.info("Job orders saved");
    }

}
