package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.catalog.model.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.catalog.service.CatalogService;
import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.pw.l0c.model.AuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
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
public class AuxService {

    private static final List<String> GIP_VIEDIR_BAND_LIST = List.of("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "8A");

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

                    final AuxProductType productType = auxFile.getAuxProductType();
                    final AuxCatalogData auxCatalogData =
                            catalogService.retrieveLatestAuxData(productType, datastrip.getSatellite(), datastrip.getStartTime(), datastrip.getStopTime())
                                    .orElseThrow(() -> new AuxQueryException("No AUX file of type " + productType.name() + " found for Datastrip " + datastrip.getName()));

                    final List<FileInfo> fileInfoList = new ArrayList<>();

                    // TODO remove this and rework that thing properly once we get bands in catalog
                    if (productType.equals(AuxProductType.GIP_VIEDIR)) {
                        fileInfoList.addAll(
                                GIP_VIEDIR_BAND_LIST.stream()
                                        .map(band -> new FileInfo()
                                                .setBucket(l0cPreparationProperties.getAuxBucket())
                                                .setKey(replaceBand(auxCatalogData.getKeyObjectStorage(), band))
                                                .setLocalPath(auxFile.getFolder().getPath() + "/" + productType)
                                                .setLocalName(replaceBand(auxCatalogData.getProductName(), band))
                                                .setType(productType.name()))
                                        .toList()
                        );
                    } else {
                        fileInfoList.add(new FileInfo()
                                .setBucket(l0cPreparationProperties.getAuxBucket())
                                .setKey(auxCatalogData.getKeyObjectStorage())
                                .setLocalPath(auxFile.getFolder().getPath() + "/" + productType)
                                .setLocalName(auxCatalogData.getProductName())
                                .setType(productType.name()));
                    }

                    return fileInfoList;
                }
        ));
    }

    private String replaceBand(String string, String band) {
        return string.substring(0, string.length() - 2) + band;
    }

}
