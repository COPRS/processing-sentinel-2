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

package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DatastripUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemManagementService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class L0cDatastripManagementService extends PWItemManagementService<L0cDatastrip, L0cDatastripEntity, L0cDatastripService> {

    public L0cDatastripManagementService(L0cDatastripService datastripService, CatalogService catalogService) {
        super(catalogService, datastripService);
    }

    @Override
    public List<L0cDatastrip> getMissingAux() {
        return getNotReady().stream()
                .filter(datastrip -> !datastrip.allAuxAvailable())
                .toList();
    }

    @Override
    public boolean isReady(L0cDatastrip item) {
        return item.allAuxAvailable();
    }


    public void create(Path datastripPath, String satellite, String stationCode, Instant t0PdgsDate) {

        final String datastripName = datastripPath.getFileName().toString();

        if (!itemService.exists(datastripName)) {

            log.info("Creating Datastrip {}", datastripName);

            // Creating required processing folders inside DT folder - Path being (...)/DT_xx/DS/<Datastrip>
            createSharedFolders(datastripPath.getParent().getParent());

            final Pair<Instant, Instant> datastripTimes = DatastripUtils.getDatastripTimes(datastripPath);

            itemService.create(datastripPath, datastripTimes.getLeft(), datastripTimes.getRight(), satellite, stationCode, t0PdgsDate);
        }
    }

    private void createSharedFolders(Path folderPath) {
        final Set<Path> folderPaths = Set.of(
                folderPath.resolve(FolderParameters.OUTPUT_FOLDER),
                folderPath.resolve(FolderParameters.AUX_FOLDER)
        );
        FileOperationUtils.createFolders(folderPaths.stream().map(Path::toString).collect(Collectors.toSet()));
    }

}
