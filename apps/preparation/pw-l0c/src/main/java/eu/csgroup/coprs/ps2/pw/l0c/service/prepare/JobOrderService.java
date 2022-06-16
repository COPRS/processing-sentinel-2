package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidInputException;
import eu.csgroup.coprs.ps2.core.common.settings.FileParameters;
import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.JobOrderFields;
import eu.csgroup.coprs.ps2.pw.l0c.settings.L0cPreparationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JobOrderService {

    private String demFolder;

    private final L0cPreparationProperties l0cPreparationProperties;

    public JobOrderService(L0cPreparationProperties l0cPreparationProperties) {
        this.l0cPreparationProperties = l0cPreparationProperties;
    }

    @PostConstruct
    public void setup() {
        final String demFolderRoot = l0cPreparationProperties.getDemFolderRoot();
        try {
            final List<Path> demFolders = FileOperationUtils.findFolders(Paths.get(demFolderRoot), FileParameters.DEM_REGEX);
            if (demFolders.size() != 1) {
                throw new InvalidInputException("Invalid DEM files number found in " + demFolderRoot);
            }
            demFolder = demFolders.get(0).toString();
        } catch (FileOperationException fileOperationException) {
            log.error("Unable to parse DEM folder", fileOperationException);
        }
    }

    public Map<String, Map<String, String>> create(Datastrip datastrip, Map<String, String> auxValues) {

        Map<String, Map<String, String>> jobOrders = new HashMap<>();

        Map<String, String> commonValues = new HashMap<>();
        commonValues.put(JobOrderFields.ACQUISITION_STATION.getPlaceholder(), datastrip.getStationCode());
        commonValues.put(JobOrderFields.PROCESSING_STATION.getPlaceholder(), JobParameters.PROC_STATION);
        commonValues.put(JobOrderFields.START_TIME.getPlaceholder(), DateUtils.toShortDate(datastrip.getStartTime()));
        commonValues.put(JobOrderFields.STOP_TIME.getPlaceholder(), DateUtils.toShortDate(datastrip.getStopTime()));
        commonValues.put(JobOrderFields.DEM_PATH.getPlaceholder(), demFolder + "/average");
        commonValues.put(JobOrderFields.DATASTRIP_NAME.getPlaceholder(), datastrip.getName());
        commonValues.put(JobOrderFields.DT_DIR.getPlaceholder(), StringUtils.substringBeforeLast(datastrip.getFolder(), "/"));
        commonValues.put(JobOrderFields.CREATION_DATE.getPlaceholder(), DateUtils.toShortDate(Instant.now()));

        commonValues.put(JobOrderFields.GRANULE_END.getPlaceholder(), String.valueOf(getGRCount(datastrip)));


        // TODO

        // Add S2A step if A

        // add DEM_GLOBEF : find int dem folder

        // @jobOrderName@       NO   => preparation, count l0u gr
        // @jobnumber@         NO
        // @detector@          NO
        // @granule_begin@    NO
        // @granule_end@      compute once, same for all bands

        //        PARALLEL_DETECTOR =
        //            FORMAT_ISP
        //            QL_GEO
        //            FORMAT_METADATA_GR_L0C
        //            OLQC-L0cGr
        //        PARALLEL_BAND =
        //            FORMAT_IMG_QL_L0

        // FOR execution :
        // OLQC-L0cGr
        // @list_l0gr_items@  |    => execution, need output first part
        // @gr_postfix@       |
        // replaced with gr_name
        // For execution:
        // l0_dsname

        // Replace in all at once, in the end

        return jobOrders;

    }


    private long getGRCount(Datastrip datastrip) {
        // There should be an equald number of GR per band, hence this computation
        // Any error here should indicate a bigger issue with that datastrip data
        return FileOperationUtils.countFiles(Paths.get(datastrip.getFolder()).getParent().resolve("GR/DB1")) / JobParameters.BAND_COUNT;
    }

    private String getGpsUtc() {
        // TODO move to FORMAT_ISP generator
        // TODO d/l aux_ut1utc, extract tai_utc -19
        return "";
    }


}
