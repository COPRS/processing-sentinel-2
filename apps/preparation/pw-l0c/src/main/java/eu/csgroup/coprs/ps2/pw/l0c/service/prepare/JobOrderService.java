package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidInputException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
import eu.csgroup.coprs.ps2.core.common.settings.PreparationParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.TemplateUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l0c.model.AuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cJobOrderFields;
import eu.csgroup.coprs.ps2.pw.l0c.settings.PWL0cTask;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class JobOrderService {

    private String demFolder;

    private final L0cPreparationProperties l0cPreparationProperties;
    private final ObsService obsService;

    public JobOrderService(L0cPreparationProperties l0cPreparationProperties, ObsService obsService) {
        this.l0cPreparationProperties = l0cPreparationProperties;
        this.obsService = obsService;
    }

    @PostConstruct
    public void setup() {
        final String demFolderRoot = l0cPreparationProperties.getDemFolderRoot();
        try {
            final List<Path> demFolders = FileOperationUtils.findFolders(Paths.get(demFolderRoot), S2FileParameters.DEM_REGEX);
            if (demFolders.size() != 1) {
                throw new InvalidInputException("Invalid DEM files number found in " + demFolderRoot);
            }
            demFolder = demFolders.get(0).toString();
            log.info("Set up JobOrderService with L0U DUMP location: {} and DEM location: {}", l0cPreparationProperties.getInputFolderRoot(), demFolder);
        } catch (FileOperationException fileOperationException) {
            log.error("Unable to parse DEM folder", fileOperationException);
        }
    }

    public Map<String, Map<String, String>> create(Datastrip datastrip, Map<AuxFile, List<FileInfo>> auxFilesByType) {

        log.info("Creating Job Orders for Datastrip {}", datastrip.getName());

        Map<String, Map<String, String>> jobOrders = new HashMap<>();

        Map<String, String> values = new HashMap<>();

        // Set basic common values from Datastrip
        values.put(L0cJobOrderFields.ACQUISITION_STATION.getPlaceholder(), datastrip.getStationCode());
        values.put(L0cJobOrderFields.PROCESSING_STATION.getPlaceholder(), JobParameters.PROC_STATION);
        values.put(L0cJobOrderFields.START_TIME.getPlaceholder(), DateUtils.toDate(datastrip.getStartTime()));
        values.put(L0cJobOrderFields.STOP_TIME.getPlaceholder(), DateUtils.toDate(datastrip.getStopTime()));
        values.put(L0cJobOrderFields.DATASTRIP_NAME.getPlaceholder(), datastrip.getName());
        values.put(L0cJobOrderFields.DT_DIR.getPlaceholder(), Paths.get(datastrip.getFolder()).getParent().toString());
        values.put(L0cJobOrderFields.CREATION_DATE.getPlaceholder(), DateUtils.toDate(Instant.now()));

        // DEM input file location is fixed
        values.put(L0cJobOrderFields.DEM_PATH.getPlaceholder(), demFolder + "/average");

        // Add AUX file info
        values.putAll(extractAuxValues(auxFilesByType));
        values.put(L0cJobOrderFields.GPS_UTC.getPlaceholder(), getGpsUtc(auxFilesByType.get(AuxFile.AUX_UT1UTC).get(0)));

        // Compute L0u GR count
        values.put(L0cJobOrderFields.GRANULE_END.getPlaceholder(), String.valueOf(getGRCount(datastrip)));


        Arrays.stream(PWL0cTask.values())
                .forEach(pwl0cTask -> {
                    if (pwl0cTask.getL0CTask().getSatellites().contains(datastrip.getSatellite().toUpperCase())) {
                        jobOrders.put(pwl0cTask.getL0CTask().name(), TemplateUtils.fillTemplates(pwl0cTask.getTemplateFolder(), values));
                    }
                });

        log.info("Finished creating Job Orders for Datastrip {}", datastrip.getName());

        return jobOrders;

    }


    private long getGRCount(Datastrip datastrip) {
        // There should be an equal number of L0U GR per band, hence this computation
        // Any error here should indicate a bigger issue with that datastrip data
        return FileOperationUtils.countFiles(Paths.get(datastrip.getFolder()).getParent().resolve("GR/DB1")) / JobParameters.BAND_COUNT;
    }

    private Map<String, String> extractAuxValues(Map<AuxFile, List<FileInfo>> auxFilesByType) {

        Map<String, String> auxValues = new HashMap<>();

        auxFilesByType.forEach((auxFile, fileInfoList) -> {

            final String placeHolder = auxFile.getPlaceHolder();
            final String extension = auxFile.getAuxProductType().getExtension();

            if (!Strings.isEmpty(placeHolder) && !CollectionUtils.isEmpty(fileInfoList)) {

                if (fileInfoList.size() == 1) {
                    auxValues.put("@" + placeHolder + "@", fileInfoList.get(0).getLocalName() + extension);
                } else {
                    for (int i = 0; i < fileInfoList.size(); i++) {
                        auxValues.put("@" + placeHolder + String.format("%02d", i + 1) + "@", fileInfoList.get(i).getLocalName() + extension);
                    }
                }
            }
        });

        return auxValues;
    }

    private String getGpsUtc(FileInfo auxUt1utcFileInfo) {

        log.debug("Fetching GPS_UTC value");

        Path tmpFolder = Paths.get(PreparationParameters.TMP_DOWNLOAD_FOLDER + "/" + UUID.randomUUID());
        Path localAuxPath = tmpFolder.resolve(auxUt1utcFileInfo.getObsName());
        Path dblPath = localAuxPath.resolve(auxUt1utcFileInfo.getObsName() + S2FileParameters.AUX_FILE_EXTENSION);

        obsService.download(auxUt1utcFileInfo.getBucket(), auxUt1utcFileInfo.getKey(), localAuxPath.toString());

        final String extractValue = FileContentUtils.extractValue(dblPath, "TAI-UTC =", List.of("TAI-UTC =", "\\..*", "[ ]*"));

        Integer value = Integer.parseInt(extractValue) - 19;

        if (!FileUtils.deleteQuietly(tmpFolder.toFile())) {
            log.warn("Unable to delete temp folder {}", tmpFolder);
        }

        log.debug("Done fetching GPS_UTC value");

        return String.valueOf(value);
    }

}
