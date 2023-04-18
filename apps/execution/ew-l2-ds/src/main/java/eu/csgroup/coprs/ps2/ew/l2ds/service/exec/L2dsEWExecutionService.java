package eu.csgroup.coprs.ps2.ew.l2ds.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFolder;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.ew.service.l2.L2EWExecutionService;
import eu.csgroup.coprs.ps2.core.ew.settings.L2EWParameters;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class L2dsEWExecutionService extends L2EWExecutionService {

    public L2dsEWExecutionService(SharedProperties sharedProperties) {
        super(sharedProperties);
    }

    @Override
    protected List<String> getCommand(L2ExecutionInput executionInput) {

        final Path dsFolderPath = Path.of(executionInput.getInputFolder(), L12Parameters.DS_FOLDER, executionInput.getDatastrip());
        final Path gippFolderPath = Path.of(executionInput.getAuxFolder(), AuxFolder.S2IPF_GIPP.getPath());

        return List.of(
                L2EWParameters.DS_SCRIPT_NAME,
                dsFolderPath.toString(),
                gippFolderPath.toString(),
                sharedProperties.getDemFolderRoot(),
                FolderParameters.WORKING_FOLDER_ROOT,
                executionInput.getOutputFolder(),
                JobParameters.PROC_STATION,
                JobParameters.PROC_STATION
        );
    }

    @Override
    public String getLevel() {
        return "L2A_DS";
    }

}
