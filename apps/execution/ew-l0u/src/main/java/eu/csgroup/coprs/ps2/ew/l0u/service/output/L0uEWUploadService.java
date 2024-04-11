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

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class L0uEWUploadService extends EWUploadService<L0uExecutionInput> {

    private final ObsBucketProperties bucketProperties;
    private final ObsService obsService;

    public L0uEWUploadService(ObsBucketProperties bucketProperties, ObsService obsService) {
        this.bucketProperties = bucketProperties;
        this.obsService = obsService;
    }

    @Override
    public Map<ProductFamily, Set<FileInfo>> upload(L0uExecutionInput executionInput, UUID parentUid) {

        log.info("Uploading AUX files to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = new EnumMap<>(ProductFamily.class);

        final Path rootPath = Paths.get(L0uFolderParameters.L0U_DUMP_PATH);

        try {
            fileInfoByFamily.putAll(buildFolderInfoInFolder(rootPath, S2FileParameters.SAD_REGEX, ProductFamily.S2_SAD, bucketProperties.getSadBucket()));
            fileInfoByFamily.putAll(buildFolderInfoInTree(rootPath, S2FileParameters.HKTM_REGEX, ProductFamily.S2_HKTM, bucketProperties.getHktmBucket()));

            final Set<FileInfo> fileInfoSet = fileInfoByFamily.values().stream().flatMap(Collection::parallelStream).collect(Collectors.toSet());
            if (fileInfoSet.isEmpty()) {
                log.warn("No AUX file to upload to OBS.");
            } else {
                obsService.uploadWithMd5(fileInfoSet, parentUid);
            }
        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded AUX files to OBS");

        return fileInfoByFamily;
    }

}
