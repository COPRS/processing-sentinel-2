package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cJobOrderFields;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.TemplateUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.settings.PWL0cTask;
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
public class L0cJobOrderService {

    private String demFolder;

    private final SharedProperties sharedProperties;
    private final ObsService obsService;

    public L0cJobOrderService(SharedProperties sharedProperties, ObsService obsService) {
        this.sharedProperties = sharedProperties;
        this.obsService = obsService;
    }

    @PostConstruct
    public void setup() {
        final String demFolderRoot = sharedProperties.getDemFolderRoot();
        final String globeFolderName = sharedProperties.getGlobeFolderName();
        demFolder = Paths.get(demFolderRoot, globeFolderName).toString();
        log.info("Set up JobOrderService with L0U DUMP location: {} and DEM location: {}", sharedProperties.getSharedFolderRoot(), demFolder);
    }

    public Map<String, Map<String, String>> create(L0cDatastrip datastrip, Map<AuxProductType, List<FileInfo>> auxFilesByType) {

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
        values.put(L0cJobOrderFields.DEM_PATH.getPlaceholder(), demFolder);

        // Add AUX file info
        values.putAll(extractAuxValues(auxFilesByType));
        values.put(L0cJobOrderFields.GPS_UTC.getPlaceholder(), getGpsUtc(auxFilesByType));

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


    private long getGRCount(L0cDatastrip datastrip) {
        // There should be an equal number of L0U GR per band, hence this computation
        // Any error here should indicate a bigger issue with that datastrip data
        return FileOperationUtils.countFiles(Paths.get(datastrip.getFolder()).getParent().resolve("GR/DB1")) / JobParameters.BAND_COUNT;
    }

    private Map<String, String> extractAuxValues(Map<AuxProductType, List<FileInfo>> auxFilesByType) {

        Map<String, String> auxValues = new HashMap<>();

        auxFilesByType.forEach((auxProductType, fileInfoList) -> {

            final String placeHolder = L0cAuxFile.valueOf(auxProductType.name()).getPlaceHolder();
            final String extension = auxProductType.getExtension();

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

    private String getGpsUtc(Map<AuxProductType, List<FileInfo>> auxFilesByType) {

        log.debug("Fetching GPS_UTC value");

        FileInfo auxUt1utcFileInfo = auxFilesByType.get(AuxProductType.AUX_UT1UTC).get(0);
        FileInfo gipDatatiFileInfo = auxFilesByType.get(AuxProductType.GIP_DATATI).get(0);

        Path tmpFolder = Paths.get(FolderParameters.TMP_DOWNLOAD_FOLDER + "/" + UUID.randomUUID());

        Path ut1utcLocalAuxPath = tmpFolder.resolve(auxUt1utcFileInfo.getObsName());
        Path ut1utcDblPath = ut1utcLocalAuxPath.resolve(auxUt1utcFileInfo.getObsName() + S2FileParameters.AUX_FILE_EXTENSION);
        Path gipDatatiLocalAuxPath = tmpFolder.resolve(gipDatatiFileInfo.getObsName());
        Path gipDatatiDblPath = gipDatatiLocalAuxPath.resolve(gipDatatiFileInfo.getObsName() + S2FileParameters.AUX_FILE_EXTENSION);

        obsService.download(Set.of(
                new FileInfo()
                        .setBucket(auxUt1utcFileInfo.getBucket())
                        .setKey(auxUt1utcFileInfo.getKey())
                        .setFullLocalPath(ut1utcLocalAuxPath.toString()),
                new FileInfo()
                        .setBucket(gipDatatiFileInfo.getBucket())
                        .setKey(gipDatatiFileInfo.getKey())
                        .setFullLocalPath(gipDatatiLocalAuxPath.toString())
        ));

        final String taiUtcValue = FileContentUtils.extractValue(ut1utcDblPath, "TAI-UTC =", List.of("TAI-UTC =", "\\..*", "[ ]*"));
        final String gpsTimeTaiValue = FileContentUtils.extractValue(gipDatatiDblPath, "GPS_TIME_TAI", List.of(".*<GPS_TIME_TAI[^>]*>-", "</GPS_TIME_TAI>"));

        Integer value = Integer.parseInt(gpsTimeTaiValue) - Integer.parseInt(taiUtcValue);

        if (!FileUtils.deleteQuietly(tmpFolder.toFile())) {
            log.warn("Unable to delete temp folder {}", tmpFolder);
        }

        log.debug("Done fetching GPS_UTC value");

        return String.valueOf(value);
    }

}
