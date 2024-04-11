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

package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.ew.service.EWDownloadService;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class L0uEWDownloadService extends EWDownloadService {

    public L0uEWDownloadService(ObsService obsService) {
        super(obsService);
    }

    @Override
    protected void prepareStandardFiles(Set<FileInfo> fileInfoSet) {

        fileInfoSet.forEach(fileInfo -> {

            fileInfo.setLocalName(fileInfo.getObsName());

            String localPath = L0uFolderParameters.INPUT_PATH;
            if (fileInfo.getObsName().contains("ch1")) {
                localPath += "/ch_1";
            } else if (fileInfo.getObsName().contains("ch2")) {
                localPath += "/ch_2";
            }

            fileInfo.setLocalPath(localPath);
        });
    }

}
