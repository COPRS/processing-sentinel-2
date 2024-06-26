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

package eu.csgroup.coprs.ps2.ew.l1sa.service.output;

import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1sEWMessageService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class L1saEWMessageService extends L1sEWMessageService {

    @Override
    protected Set<String> getCustomOutputs(String outputFolder) {
        final Path rootPath = Paths.get(outputFolder);
        return FileOperationUtils.findFoldersInTree(rootPath, S2FileParameters.L1_DS_REGEX)
                .stream()
                .map(path -> path.getFileName().toString() + FolderParameters.TMP_DS_SUFFIX_L1SA)
                .collect(Collectors.toSet());
    }

}
