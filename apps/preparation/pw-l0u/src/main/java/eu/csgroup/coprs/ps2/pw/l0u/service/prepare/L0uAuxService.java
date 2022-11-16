package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l0u.config.L0uPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0u.model.AuxValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
public class L0uAuxService {

    private final CatalogService catalogService;
    private final L0uPreparationProperties l0uPreparationProperties;
    private final ObsService obsService;

    public L0uAuxService(CatalogService catalogService, L0uPreparationProperties l0uPreparationProperties, ObsService obsService) {
        this.catalogService = catalogService;
        this.l0uPreparationProperties = l0uPreparationProperties;
        this.obsService = obsService;
    }


    public Map<AuxValue, String> getValues(String satellite, Instant from, Instant to) {

        log.info("Extracting values from AUX files");

        Map<AuxValue, String> infoByAuxValue = new EnumMap<>(AuxValue.class);
        String tmpFolder = FolderParameters.TMP_DOWNLOAD_FOLDER + "/" + UUID.randomUUID();

        Map<AuxValue, Path> auxPathByAuxValue = downloadAuxFiles(satellite, from, to, tmpFolder);

        auxPathByAuxValue.forEach((auxValue, auxPath) -> infoByAuxValue.put(auxValue, extractValue(auxValue, auxPath)));

        if (!FileUtils.deleteQuietly(new File(tmpFolder))) {
            log.warn("Unable to delete temp folder {}", tmpFolder);
        }

        log.info("Finished extracting values from AUX files");

        return infoByAuxValue;
    }

    private Map<AuxValue, Path> downloadAuxFiles(String satellite, Instant from, Instant to, String tmpFolder) {

        Map<AuxValue, String> keyByAuxValue = new EnumMap<>(AuxValue.class);

        // Retrieve aux data from MetaDataCatalog
        Arrays.stream(AuxValue.values())
                .forEach(auxValue ->
                        keyByAuxValue.put(
                                auxValue,
                                catalogService.retrieveLatestAuxData(auxValue.getAuxProductType(), satellite, from, to)
                                        .orElseThrow(() -> new AuxQueryException("Unable to find AUX file for " + auxValue.getAuxProductType().name()))
                                        .getKeyObjectStorage())
                );

        // Download all files to tmp folder
        obsService.download(keyByAuxValue.values()
                .stream()
                .map(key -> new FileInfo()
                        .setBucket(l0uPreparationProperties.getAuxBucket())
                        .setKey(key)
                        .setLocalName(key)
                        .setLocalPath(tmpFolder))
                .collect(Collectors.toSet()));

        return keyByAuxValue.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Paths.get(tmpFolder, entry.getValue(), entry.getValue() + S2FileParameters.AUX_FILE_EXTENSION)));
    }

    private String extractValue(AuxValue auxValue, Path auxPath) {
        return auxValue.getPrefix() + FileContentUtils.extractValue(auxPath, auxValue.getLineFilter(), auxValue.getRegexList());
    }

}
