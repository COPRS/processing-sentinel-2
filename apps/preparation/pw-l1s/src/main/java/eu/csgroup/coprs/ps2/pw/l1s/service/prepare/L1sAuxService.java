package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.pw.service.PWAuxService;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sAuxFile;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class L1sAuxService extends PWAuxService<L1sDatastrip> {

    public L1sAuxService(CatalogService catalogService, ObsBucketProperties bucketProperties, SharedProperties sharedProperties) {
        super(catalogService, bucketProperties, sharedProperties);
    }

    @Override
    protected String getAuxPath(AuxProductType auxProductType, L1sDatastrip item) {
        final Path auxPath = Paths.get(sharedProperties.getSharedFolderRoot(), item.getFolder(), FolderParameters.AUX_FOLDER);
        return auxPath.resolve(L1sAuxFile.valueOf(auxProductType.name()).getFolder().getPath()).toString();
    }

}
