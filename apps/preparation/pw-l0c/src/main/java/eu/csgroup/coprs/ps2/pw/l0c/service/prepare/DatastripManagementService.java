package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.service.pw.PWItemManagementService;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Slf4j
@Service
@Transactional
public class DatastripManagementService extends PWItemManagementService<Datastrip, DatastripService> {

    private static final String START_TIME_TAG = "DATASTRIP_SENSING_START";
    private static final String STOP_TIME_TAG = "DATASTRIP_SENSING_STOP";
    private static final long DELETION_GRACE_PERIOD = 60L;

    public DatastripManagementService(DatastripService datastripService, CatalogService catalogService) {
        super(catalogService, datastripService);
    }


    @Override
    public List<Datastrip> getReady() {
        return itemService.readAll(true, false);
    }

    @Override
    public List<Datastrip> getDeletable() {
        final Instant now = Instant.now();
        return itemService.readAll(true).stream()
                .filter(datastrip -> Duration.between(datastrip.getLastModifiedDate(), now).getSeconds() > DELETION_GRACE_PERIOD)
                .toList();
    }

    @Override
    public void updateAvailableAux() {

        log.info("Updating AUX availability for all waiting datastrips");

        final List<Datastrip> missingAux = getNotReady().stream()
                .filter(datastrip -> !datastrip.allAuxAvailable())
                .toList();

        log.debug("Found {} datastrips with missing AUX", missingAux.size());

        if (!CollectionUtils.isEmpty(missingAux)) {
            missingAux.forEach(this::updateAvailableAux);
            itemService.updateAll(missingAux);
        }

        log.info("Finished updating AUX availability for all waiting datastrips");
    }

    @Override
    public void updateNotReady() {

        log.info("Updating ready status for all Datastrips not yet ready");

        final List<Datastrip> datastrips = getNotReady();

        log.debug("Found {} datastrips not ready", datastrips.size());

        if (!CollectionUtils.isEmpty(datastrips)) {
            datastrips.forEach(datastrip -> {
                datastrip.setReady(datastrip.allAuxAvailable());
                if (datastrip.isReady()) {
                    log.info("Datastrip {} is now ready", datastrip.getName());
                }
            });
            itemService.updateAll(datastrips);
        }
        log.info("Finished updating ready status for all datastrips not yet ready");
    }

    @Override
    public void setJobOrderCreated(List<Datastrip> datastripList) {
        datastripList.forEach(datastrip -> datastrip.setJobOrderCreated(true));
        itemService.updateAll(datastripList);
    }

    public void create(Path datastripPath, String satellite, String stationCode, Instant t0PdgsDate) {

        final String datastripName = datastripPath.getFileName().toString();

        if (!itemService.exists(datastripName)) {

            log.info("Creating Datastrip {}", datastripName);

            final String datastripFolder = datastripPath.getParent().toString();
            final String datastripXmlName = datastripName
                    .replace(S2FileParameters.L0U_DS_SUFFIX, ".xml")
                    .replace("_MSI_", "_MTD_");
            final Path datastripXmlPath = datastripPath.resolve(datastripXmlName);

            Instant startTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, START_TIME_TAG));
            Instant stopTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, STOP_TIME_TAG));

            itemService.create(datastripName, datastripFolder, startTime, stopTime, satellite, stationCode, t0PdgsDate);
        }
    }

    private List<Datastrip> getNotReady() {
        return itemService.readAll(false, false);
    }

}
