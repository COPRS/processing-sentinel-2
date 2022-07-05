package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0c.model.AuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuxService {

    private final CatalogService catalogService;
    private final L0cPreparationProperties l0cPreparationProperties;

    public AuxService(CatalogService catalogService, L0cPreparationProperties l0cPreparationProperties) {
        this.catalogService = catalogService;
        this.l0cPreparationProperties = l0cPreparationProperties;
    }

    public Map<AuxFile, List<FileInfo>> getAux(Datastrip datastrip) {

        return Arrays.stream(AuxFile.values()).collect(Collectors.toMap(
                Function.identity(),
                auxFile -> {

                    final List<FileInfo> fileInfoList = new ArrayList<>();

                    final List<String> bandList = auxFile.getAuxProductType().getBandList();

                    if (CollectionUtils.isEmpty(bandList)) {
                        fileInfoList.add(getFileInfo(auxFile, datastrip, null));
                    } else {
                        bandList.forEach(bandIndexId -> fileInfoList.add(getFileInfo(auxFile, datastrip, bandIndexId)));
                    }

                    return fileInfoList;
                }
        ));
    }

    private FileInfo getFileInfo(AuxFile auxFile, Datastrip datastrip, String bandIndexId) {

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
