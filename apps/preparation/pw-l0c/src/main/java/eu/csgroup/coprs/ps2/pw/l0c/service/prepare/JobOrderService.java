package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.catalog.service.CatalogService;
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

    private final CatalogService catalogService;
    private final L0cPreparationProperties l0cPreparationProperties;

    public JobOrderService(CatalogService catalogService, L0cPreparationProperties l0cPreparationProperties) {
        this.catalogService = catalogService;
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

        // TODO
        // Insert into templates
        // Use parallel detector or band
        // Add S2A step if A

        // add DEM_GLOBEF : find int dem folder


        // @jobOrderName@
        // @jobnumber@

        // @detector@
        // @granule_begin@
        // @granule_end@

        // @list_l0gr_items@
        // @gr_postfix@

        return jobOrders;

    }

    private String getGpsUtc() {
        // TODO move to FORMAT_ISP generator
        // TODO d/l aux_ut1utc, extract tai_utc -19
        return "";
    }


}
