package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.model.processing.Band;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L0cAuxService {

    private final CatalogService catalogService;
    private final L0cPreparationProperties l0cPreparationProperties;

    public L0cAuxService(CatalogService catalogService, L0cPreparationProperties l0cPreparationProperties) {
        this.catalogService = catalogService;
        this.l0cPreparationProperties = l0cPreparationProperties;
    }

    public Map<L0cAuxFile, List<FileInfo>> getAux(L0cDatastrip datastrip) {

        return Arrays.stream(L0cAuxFile.values()).collect(Collectors.toMap(
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

    private FileInfo getFileInfo(L0cAuxFile auxFile, L0cDatastrip datastrip, String bandIndexId) {

        final AuxProductType productType = auxFile.getAuxProductType();

        final AuxCatalogData auxCatalogData =
                catalogService.retrieveLatestAuxData(productType, datastrip.getSatellite(), datastrip.getStartTime(), datastrip.getStopTime(), bandIndexId)
                        .orElseThrow(() -> new AuxQueryException("No AUX file of type " + productType.name() + " found for Datastrip " + datastrip.getName()));

        return new FileInfo()
                .setBucket(l0cPreparationProperties.getAuxBucket())
                .setKey(auxCatalogData.getKeyObjectStorage())
                .setLocalPath(auxFile.getFolder().getPath() + "/" + productType)
                .setLocalName(auxCatalogData.getProductName())
                .setType(productType.name());

    }

}
