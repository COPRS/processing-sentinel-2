package eu.csgroup.coprs.ps2.ew.l0u.service.exec;

import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class ExecutionService {

    public void execute(L0uExecutionInput l0uExecutionInput) {

        log.info("Starting L0U processing");

        String jobOrderName = l0uExecutionInput.getJobOrders().keySet().iterator().next();

        final Integer exitCode = ScriptUtils.run(
                new ScriptWrapper()
                        .setRunId(jobOrderName)
                        .setWorkdir(L0uFolderParameters.WORKSPACE_PATH)
                        .setCommand(List.of(L0uFolderParameters.SCRIPT_PATH, L0uFolderParameters.WORKSPACE_PATH + "/" + jobOrderName))
        );

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
