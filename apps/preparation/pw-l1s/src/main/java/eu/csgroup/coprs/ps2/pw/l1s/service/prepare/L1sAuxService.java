package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.model.processing.Band;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.pw.l1s.config.L1sPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sAuxFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1sAuxService {

    private final CatalogService catalogService;
    private final L1sPreparationProperties l1sPreparationProperties;

    public L1sAuxService(CatalogService catalogService, L1sPreparationProperties l1sPreparationProperties) {
        this.catalogService = catalogService;
        this.l1sPreparationProperties = l1sPreparationProperties;
    }

    public Map<L1sAuxFile, List<FileInfo>> getAux(L1sDatastrip datastrip) {

        return Arrays.stream(L1sAuxFile.values()).collect(Collectors.toMap(
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

    private FileInfo getFileInfo(L1sAuxFile auxFile, L1sDatastrip datastrip, String bandIndexId) {

        final Path auxPath = Paths.get(l1sPreparationProperties.getSharedFolderRoot(), datastrip.getFolder(), L1Parameters.AUX_FOLDER);
        final AuxProductType productType = auxFile.getAuxProductType();

        final AuxCatalogData auxCatalogData =
                catalogService.retrieveLatestAuxData(productType, datastrip.getSatellite(), datastrip.getStartTime(), datastrip.getStopTime(), bandIndexId)

                        .orElseThrow(() -> new AuxQueryException("No AUX file of type " + productType.name() + " found for Datastrip " + datastrip.getName()));
        return new FileInfo()
                .setBucket(l1sPreparationProperties.getAuxBucket())
                .setKey(auxCatalogData.getKeyObjectStorage())
                .setLocalPath(auxPath.resolve(auxFile.getFolder().getPath()).toString())
                .setLocalName(auxCatalogData.getProductName())
                .setProductFamily(ProductFamily.S2_AUX)
                .setAuxProductType(productType);
    }

}
