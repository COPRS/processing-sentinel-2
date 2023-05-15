package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.pw.service.PWAuxService;
import eu.csgroup.coprs.ps2.pw.l2.model.L2AuxFile;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class L2AuxService extends PWAuxService<L2Datastrip> {

    public L2AuxService(CatalogService catalogService, ObsBucketProperties bucketProperties, SharedProperties sharedProperties) {
        super(catalogService, bucketProperties, sharedProperties);
    }

    @Override
    protected String getAuxPath(AuxProductType auxProductType, L2Datastrip item) {
        final Path auxPath = Paths.get(sharedProperties.getSharedFolderRoot(), item.getFolder(), FolderParameters.AUX_FOLDER);
        return auxPath.resolve(L2AuxFile.valueOf(auxProductType.name()).getFolder().getPath()).toString();
    }

}
