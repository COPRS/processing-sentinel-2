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

package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.pw.service.PWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class L2PWExecutionInputService implements PWExecutionInputService<L2ExecutionInput, L2Datastrip> {

    private final SharedProperties sharedProperties;
    private final L2AuxService auxService;

    public L2PWExecutionInputService(SharedProperties sharedProperties, L2AuxService auxService) {
        this.sharedProperties = sharedProperties;
        this.auxService = auxService;
    }

    @Override
    public List<L2ExecutionInput> create(List<L2Datastrip> itemList) {

        log.info("Creating output payload for all ready Datastrips ({})", itemList.size());

        final List<L2ExecutionInput> l2ExecutionInputs = itemList.stream().map(this::create).toList();

        log.info("Finished creating output payload for all ready Datastrips ({})", itemList.size());

        return l2ExecutionInputs;
    }

    private L2ExecutionInput create(L2Datastrip datastrip) {

        log.info("Building execution input for Datastrip {}", datastrip.getName());

        final Path rootPath = Paths.get(sharedProperties.getSharedFolderRoot(), datastrip.getFolder());
        final Path inputPath = rootPath.resolve(FolderParameters.INPUT_FOLDER);

        final L2ExecutionInput executionInput = new L2ExecutionInput();
        executionInput
                .setTileList(datastrip.getAvailableByTL().keySet().stream().toList())
                .setDatastrip(datastrip.getName())
                .setInputFolder(inputPath.toString())
                .setOutputFolder(rootPath.resolve(FolderParameters.OUTPUT_FOLDER).toString())
                .setAuxFolder(rootPath.resolve(FolderParameters.AUX_FOLDER).toString())
                .setSatellite(datastrip.getSatellite())
                .setStation(datastrip.getStationCode())
                .setStartTime(datastrip.getStartTime())
                .setStopTime(datastrip.getStopTime())
                .setT0PdgsDate(datastrip.getT0PdgsDate());

        executionInput.setFiles(auxService.getAux(datastrip).values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

        log.info("Finished building execution input for Datastrip {}", datastrip.getName());

        return executionInput;
    }

}
