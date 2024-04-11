/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.ew.l2ds.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFolder;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
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

        final Path dsFolderPath = Path.of(executionInput.getInputFolder(), FolderParameters.DS_FOLDER, executionInput.getDatastrip());
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
