package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.ew.service.EWJobOrderService;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;


@Slf4j
@Component
public class L0uEWJobOrderService extends EWJobOrderService<L0uExecutionInput> {

    public void saveJobOrders(L0uExecutionInput l0uExecutionInput) {

        log.info("Saving job orders to files");

        save(l0uExecutionInput.getJobOrders(), Paths.get(FolderParameters.WORKING_FOLDER_ROOT));

        log.info("Job orders saved");
    }

}
