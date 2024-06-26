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

package eu.csgroup.coprs.ps2.ew.l1ab.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1abEWUploadService extends EWUploadService<L1ExecutionInput> {

    private final ObsBucketProperties bucketProperties;
    private final ObsService obsService;

    public L1abEWUploadService(ObsBucketProperties bucketProperties, ObsService obsService) {
        this.bucketProperties = bucketProperties;
        this.obsService = obsService;
    }

    @Override
    public Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput, UUID parentUid) {

        log.info("Uploading L1A and/or L1B files to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = new EnumMap<>(ProductFamily.class);
        final Path rootPath = Paths.get(FolderParameters.WORKING_FOLDER_ROOT);

        try {

            fileInfoByFamily.putAll(
                    buildFolderInfoInTree(rootPath.resolve(FolderParameters.L1A_DS_ROOT), S2FileParameters.L1A_DS_REGEX, ProductFamily.S2_L1A_DS, bucketProperties.getL1DSBucket()));
            fileInfoByFamily.putAll(
                    buildFolderInfoInTree(rootPath.resolve(FolderParameters.L1A_GR_ROOT), S2FileParameters.L1A_GR_REGEX, ProductFamily.S2_L1A_GR, bucketProperties.getL1GRBucket()));
            fileInfoByFamily.putAll(
                    buildFolderInfoInTree(rootPath.resolve(FolderParameters.L1B_DS_ROOT), S2FileParameters.L1B_DS_REGEX, ProductFamily.S2_L1B_DS, bucketProperties.getL1DSBucket()));
            fileInfoByFamily.putAll(
                    buildFolderInfoInTree(rootPath.resolve(FolderParameters.L1B_GR_ROOT), S2FileParameters.L1B_GR_REGEX, ProductFamily.S2_L1B_GR, bucketProperties.getL1GRBucket()));

            obsService.uploadWithMd5(fileInfoByFamily.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()), parentUid);
        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded L1A and/or L1B files to OBS");

        return fileInfoByFamily;
    }

}
