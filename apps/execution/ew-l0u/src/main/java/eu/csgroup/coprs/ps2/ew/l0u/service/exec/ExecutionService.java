package eu.csgroup.coprs.ps2.ew.l0u.service.exec;

import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import eu.csgroup.coprs.ps2.ew.l0u.settings.FolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ExecutionService {

    public void execute(String jobOrderName) {

        log.info("Starting L0U processing");

        final Integer exitCode = ScriptUtils.run(
                FolderParameters.WORKSPACE_PATH,
                FolderParameters.INSTALL_SCRIPT_PATH + "/launch_eisp_ing_typ.bash",
                FolderParameters.WORKSPACE_PATH + "/" + jobOrderName);

        // TODO Check return values of that script

        if (exitCode == 0) {
            log.info("Finished L0U processing");
        } else {
            if (exitCode < 128) {
                log.warn("Finished L0U processing with a warning");
            } else {
                final String message = "Finished L0U processing with an error";
                log.error(message);
                throw new ScriptExecutionException(message);
            }
        }
    }

}
