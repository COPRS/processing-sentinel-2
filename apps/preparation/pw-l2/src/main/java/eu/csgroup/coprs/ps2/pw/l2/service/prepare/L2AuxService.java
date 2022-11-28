package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.model.processing.Band;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.pw.l2.model.L2AuxFile;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L2AuxService {

    private final CatalogService catalogService;
    private final SharedProperties sharedProperties;
    private final ObsBucketProperties bucketProperties;

    public L2AuxService(CatalogService catalogService, SharedProperties sharedProperties, ObsBucketProperties bucketProperties) {
        this.catalogService = catalogService;
        this.sharedProperties = sharedProperties;
        this.bucketProperties = bucketProperties;
    }

    public Map<L2AuxFile, List<FileInfo>> getAux(L2Datastrip datastrip) {

        // Fetching only available AUX, since some are optional for L2
        return datastrip.getAvailableByAux()
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(entry -> L2AuxFile.valueOf(entry.getKey()))
                .collect(Collectors.toMap(
                        Function.identity(),
                        auxFile -> {

                            final List<FileInfo> fileInfoList = new ArrayList<>();

                            if (auxFile.getAuxProductType().isBandDependent()) {
                                Band.allBandIndexIds().forEach(bandIndexId -> fileInfoList.add(getFileInfo(auxFile, datastrip, bandIndexId)));
                            } else {
                                fileInfoList.add(getFileInfo(auxFile, datastrip, null));
                            }

                            return fileInfoList;
                        }
                ));
    }

    private FileInfo getFileInfo(L2AuxFile auxFile, L2Datastrip datastrip, String bandIndexId) {

        final Path auxPath = Paths.get(sharedProperties.getSharedFolderRoot(), datastrip.getFolder(), L12Parameters.AUX_FOLDER);
        final AuxProductType productType = auxFile.getAuxProductType();

        final AuxCatalogData auxCatalogData =
                catalogService.retrieveLatestAuxData(productType, datastrip.getSatellite(), datastrip.getStartTime(), datastrip.getStopTime(), bandIndexId)
                        .orElseThrow(() -> new AuxQueryException("No AUX file of type " + productType.name() + " found for Datastrip " + datastrip.getName()));
        return new FileInfo()
                .setBucket(bucketProperties.getAuxBucket())
                .setKey(auxCatalogData.getKeyObjectStorage())
                .setLocalPath(auxPath.resolve(auxFile.getFolder().getPath()).toString())
                .setLocalName(auxCatalogData.getProductName())
                .setProductFamily(ProductFamily.S2_AUX)
                .setAuxProductType(productType);
    }

}
