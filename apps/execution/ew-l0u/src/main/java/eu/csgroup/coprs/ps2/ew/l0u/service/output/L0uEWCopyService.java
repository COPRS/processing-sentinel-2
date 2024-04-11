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

package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Slf4j
@Component
public class L0uEWCopyService {

    private final SharedProperties sharedProperties;

    public L0uEWCopyService(SharedProperties sharedProperties) {
        this.sharedProperties = sharedProperties;
    }

    public String copy() {

        log.info("Copying output files to shared disk");

        String outputFolder = null;

        final Path rootPath = Paths.get(L0uFolderParameters.L0U_DUMP_PATH);

        final List<Path> dtFolders = FileOperationUtils.findFolders(rootPath, S2FileParameters.DT_REGEX);

        log.info("Found {} DT folders", dtFolders.size());

        if (!dtFolders.isEmpty()) {

            outputFolder = UUID.randomUUID().toString();
            final Path destPath = Paths.get(sharedProperties.getSharedFolderRoot(), outputFolder);

            dtFolders.forEach(path -> {
                try {
                    FileUtils.copyDirectoryToDirectory(path.toFile(), destPath.toFile());
                } catch (IOException e) {
                    throw new FileOperationException("Unable to copy files to shared disk", e);
                }
            });
        }
        log.info("Finished copying output files to shared disk");

        return outputFolder;
    }

}
