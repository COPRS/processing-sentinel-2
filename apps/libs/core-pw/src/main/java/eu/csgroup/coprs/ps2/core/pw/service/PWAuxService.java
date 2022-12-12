package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.model.processing.Band;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.pw.model.PWItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PWAuxService<S extends PWItem> {

    protected final CatalogService catalogService;
    protected final ObsBucketProperties bucketProperties;
    protected final SharedProperties sharedProperties;

    protected PWAuxService(CatalogService catalogService, ObsBucketProperties bucketProperties, SharedProperties sharedProperties) {
        this.catalogService = catalogService;
        this.bucketProperties = bucketProperties;
        this.sharedProperties = sharedProperties;
    }

    public Map<AuxProductType, List<FileInfo>> getAux(S item) {
        return item.getAvailableByAux()
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(entry -> AuxProductType.valueOf(entry.getKey()))
                .collect(Collectors.toMap(
                        Function.identity(),
                        auxProductType -> {

                            final List<FileInfo> fileInfoList = new ArrayList<>();

                            if (auxProductType.isBandDependent()) {
                                Band.allBandIndexIds().forEach(bandIndexId -> fileInfoList.add(getFileInfo(auxProductType, item, bandIndexId)));
                            } else {
                                fileInfoList.add(getFileInfo(auxProductType, item, null));
                            }

                            return fileInfoList;
                        }
                ));
    }

    protected abstract String getAuxPath(AuxProductType auxProductType, S item);

    private FileInfo getFileInfo(AuxProductType auxProductType, S item, String bandIndexId) {

        final AuxCatalogData auxCatalogData =
                catalogService.retrieveLatestAuxData(auxProductType, item.getSatellite(), item.getStartTime(), item.getStopTime(), bandIndexId)
                        .orElseThrow(() -> new AuxQueryException("No AUX file of type " + auxProductType.name() + " found for Datastrip " + item.getName()));

        return new FileInfo()
                .setBucket(bucketProperties.getAuxBucket())
                .setKey(auxCatalogData.getKeyObjectStorage())
                .setLocalPath(getAuxPath(auxProductType, item))
                .setLocalName(auxCatalogData.getProductName())
                .setProductFamily(ProductFamily.S2_AUX)
                .setAuxProductType(auxProductType);
    }

}
