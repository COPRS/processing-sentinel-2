package eu.csgroup.coprs.ps2.ew.l1s.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L1sEWCleanupService implements EWCleanupService<L1ExecutionInput> {

    @Override
    public void cleanAndPrepare() {

        log.info("Cleaning and setting up workspace");

        FileOperationUtils.deleteFolderContent(L1Parameters.WORKING_FOLDER_ROOT);

        log.info("Finished cleaning and setting up workspace");
    }

    @Override
    public void clean(L1ExecutionInput executionInput) {

        log.info("Cleaning up workspace");

        FileOperationUtils.deleteFolderContent(L1Parameters.WORKING_FOLDER_ROOT);

        log.info("Finished cleaning up workspace");
    }

}
