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

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.pw.service.PWAuxService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
public class L0cAuxService extends PWAuxService<L0cDatastrip> {

    public L0cAuxService(CatalogService catalogService, ObsBucketProperties bucketProperties, SharedProperties sharedProperties) {
        super(catalogService, bucketProperties, sharedProperties);
    }

    @Override
    protected String getAuxPath(AuxProductType auxProductType, L0cDatastrip item) {
        final Path auxPath = Path.of(item.getDtFolder(), FolderParameters.AUX_FOLDER);
        return auxPath.resolve(L0cAuxFile.valueOf(auxProductType.name()).getFolder().getPath()).toString();
    }

}
