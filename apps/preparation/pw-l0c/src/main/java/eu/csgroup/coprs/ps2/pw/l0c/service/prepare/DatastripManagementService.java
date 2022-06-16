package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.catalog.model.AuxProductType;
import eu.csgroup.coprs.ps2.core.catalog.service.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.FileParameters;
import eu.csgroup.coprs.ps2.core.common.settings.PreparationParameters;
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
public class DatastripManagementService {

    private static final String START_TIME_TAG = "DATASTRIP_SENSING_START";
    private static final String STOP_TIME_TAG = "DATASTRIP_SENSING_STOP";

    private final DatastripService datastripService;
    private final CatalogService catalogService;

    public DatastripManagementService(DatastripService datastripService, CatalogService catalogService) {
        this.datastripService = datastripService;
        this.catalogService = catalogService;
    }


    @Transactional
    public List<Datastrip> getReady() {
        return datastripService.readAll(true, false, false);
    }

    @Transactional
    public List<Datastrip> getNotReady() {
        return datastripService.readAll(false, false, false);
    }

    @Transactional
    public void create(Path datastripPath, String satellite, String stationCode) {

        final String datastripName = datastripPath.getFileName().toString();

        if (!datastripService.exists(datastripName)) {

            log.info("Creating Datastrip {}", datastripName);

            final String datastripFolder = datastripPath.getParent().toString();
            final String datastripXmlName = datastripName.replace(FileParameters.DS_SUFFIX, ".xml");
            final Path datastripXmlPath = datastripPath.resolve(datastripXmlName);

            Instant startTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, START_TIME_TAG));
            Instant stopTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, STOP_TIME_TAG));

            final Datastrip datastrip = datastripService.create(datastripName, datastripFolder, startTime, stopTime, satellite, stationCode);

            updateAvailableAux(datastrip);
        }
    }

    @Transactional
    public void updateAvailableAux() {

        log.info("Updating AUX availability for all sessions");

        final List<Datastrip> missingAux = getNotReady();

        log.debug("Found {} sessions with missing AUX", missingAux.size());

        if (!CollectionUtils.isEmpty(missingAux)) {
            missingAux.forEach(this::updateAvailableAux);
            datastripService.updateAll(missingAux);
        }

        log.info("Finished updating AUX availability for all datastrips");

    }

    @Transactional
    public void updateNotReady() {

        log.info("Updating ready and failed status for all Datastrips not yet ready");

        final List<Datastrip> datastrips = getNotReady();

        log.debug("Found {} datastrips not ready", datastrips.size());

        if (!CollectionUtils.isEmpty(datastrips)) {
            datastrips.forEach(session -> {
                // TODO maybe reverse order
                updateReadyStatus(session);
                updateFailedStatus(session);
            });
            datastripService.updateAll(datastrips);
        }
        log.info("Finished updating ready and failed status for all datastrips not yet ready");
    }

    @Transactional
    public void setJobOrderCreated(List<Datastrip> datastripList) {
        datastripList.forEach(datastrip -> datastrip.setJobOrderCreated(true));
        datastripService.updateAll(datastripList);
    }

    private void updateReadyStatus(Datastrip datastrip) {
        datastrip.setReady(datastrip.allAuxAvailable());
    }

    private void updateFailedStatus(Datastrip datastrip) {
        // TODO test and check
        if (!datastrip.isReady()) {
            final Instant creationDate = datastrip.getCreationDate();
            if (Duration.between(creationDate, Instant.now()).toHours() > PreparationParameters.FAILED_DELAY) {
                datastrip.setFailed(true);
            }
        }
    }

    private void updateAvailableAux(Datastrip datastrip) {

        log.debug("Updating AUX availability for datastrip {}", datastrip.getName());

        if (!datastrip.allAuxAvailable()) {

            datastrip.getAvailableByAux()
                    .entrySet()
                    .forEach(entry -> {

                        AuxProductType auxProductType = AuxProductType.valueOf(entry.getKey());

                        // TODO modify when band query is available with catalog

                        if (Boolean.FALSE.equals(entry.getValue())) {
                            catalogService.retrieveLatestAuxData(
                                            auxProductType,
                                            datastrip.getSatellite(),
                                            datastrip.getStartTime(),
                                            datastrip.getStopTime())
                                    .ifPresent(auxCatalogData -> entry.setValue(Boolean.TRUE));
                        }
                    });
        }

        log.debug("Finished updating AUX availability for datastrip {}", datastrip.getName());
    }

}
