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

package eu.csgroup.coprs.ps2.ew.l1sa.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ArchiveUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWDownloadService;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
@Service
public class L1saEWDownloadService extends EWDownloadService {

    private static final String AUX_ECMWFD_REGEX = "S2D.*";

    public L1saEWDownloadService(ObsService obsService) {
        super(obsService);
    }

    @Override
    protected Predicate<FileInfo> customAux() {
        return fileInfo -> fileInfo.getAuxProductType().equals(AuxProductType.AUX_CAMSFO)
                || fileInfo.getAuxProductType().equals(AuxProductType.AUX_ECMWFD);
    }

    @Override
    protected void downloadCustomAux(Set<FileInfo> fileInfoSet, UUID parentUid) {

        if (!fileInfoSet.isEmpty()) {

            obsService.download(fileInfoSet, parentUid);

            fileInfoSet.forEach(fileInfo -> {

                switch (fileInfo.getAuxProductType()) {

                    case AUX_CAMSFO -> {
                        final String dblName = fileInfo.getLocalName() + S2FileParameters.AUX_FILE_EXTENSION;
                        final Path dblPath = Paths.get(fileInfo.getFullLocalPath(), dblName);
                        ArchiveUtils.unTar(dblPath.toString(), true);
                    }

                    case AUX_ECMWFD -> {

                        final String localPath = fileInfo.getLocalPath();
                        final String downloadName = fileInfo.getLocalName();
                        final String dblName = downloadName + S2FileParameters.AUX_FILE_EXTENSION;

                        final Path tmpPath = Paths.get(localPath, downloadName + "_TMP");
                        FileOperationUtils.move(Paths.get(fileInfo.getFullLocalPath()), tmpPath);

                        final Path dblPath = tmpPath.resolve(dblName);
                        ArchiveUtils.unTar(dblPath.toString(), true);

                        FileOperationUtils.mergeFiles(tmpPath, Paths.get(fileInfo.getFullLocalPath()), AUX_ECMWFD_REGEX);

                        log.info("Cleaning up temporary files");
                        FileOperationUtils.deleteFolders(Set.of(tmpPath.toString()));
                        log.info("Finished cleaning up temporary files");
                    }

                    default -> log.warn("Something went horribly wrong");
                }
            });
        }
    }

}
