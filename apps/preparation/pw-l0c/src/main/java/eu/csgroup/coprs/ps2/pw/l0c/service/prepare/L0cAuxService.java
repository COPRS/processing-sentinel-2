package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.pw.service.PWAuxService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L0cAuxService extends PWAuxService<L0cDatastrip> {

    public L0cAuxService(CatalogService catalogService, ObsBucketProperties bucketProperties, SharedProperties sharedProperties) {
        super(catalogService, bucketProperties, sharedProperties);
    }

    @Override
    protected String getAuxPath(AuxProductType auxProductType, L0cDatastrip item) {
        return L0cAuxFile.valueOf(auxProductType.name()).getFolder().getPath() + "/" + auxProductType.name();
    }

}
