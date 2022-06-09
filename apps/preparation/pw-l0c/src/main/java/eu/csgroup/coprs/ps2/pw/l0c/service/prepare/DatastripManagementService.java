package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.catalog.service.CatalogService;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
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
    private final L0cPreparationProperties l0cPreparationProperties;

    public DatastripManagementService(DatastripService datastripService, CatalogService catalogService, L0cPreparationProperties l0cPreparationProperties) {
        this.datastripService = datastripService;
        this.catalogService = catalogService;
        this.l0cPreparationProperties = l0cPreparationProperties;
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
    public List<Datastrip> getWaiting() {
        return datastripService.readAll(false, false);
    }

    @Transactional
    public void create(Path datastripPath, String satellite, String stationCode) {

        final String datastripName = datastripPath.getFileName().toString();

        if (!datastripService.exists(datastripName)) {

            log.info("Creating Datastrip {}", datastripName);

            final String datastripFolder = datastripPath.getParent().toString();
            final String datastripXmlName = datastripName
                    .replace(S2FileParameters.L0U_DS_SUFFIX, ".xml")
                    .replace("_MSI_", "_MTD_");
            final Path datastripXmlPath = datastripPath.resolve(datastripXmlName);

            Instant startTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, START_TIME_TAG));
            Instant stopTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, STOP_TIME_TAG));

            datastripService.create(datastripName, datastripFolder, startTime, stopTime, satellite, stationCode);
        }
    }

    @Transactional
    public void updateFailed() {

        log.info("Updating failed status for all waiting datastrips");

        final List<Datastrip> waitingDatastrips = getWaiting();

        log.debug("Found {} waiting datastrips", waitingDatastrips.size());

        if (!CollectionUtils.isEmpty(waitingDatastrips)) {
            waitingDatastrips.forEach(datastrip -> {
                final Instant creationDate = datastrip.getCreationDate();
                if (Duration.between(creationDate, Instant.now()).toHours() > l0cPreparationProperties.getFailedDelay()) {
                    log.info("Failing datastrip {}", datastrip.getName());
                    datastrip.setFailed(true);
                }
            });
            datastripService.updateAll(waitingDatastrips);
        }

        log.info("Finished updating failed status for all waiting datastrips");
    }

    @Transactional
    public void updateAvailableAux() {

        log.info("Updating AUX availability for all waiting datastrips");

        final List<Datastrip> missingAux = getNotReady().stream()
                .filter(datastrip -> !datastrip.allAuxAvailable())
                .toList();

        log.debug("Found {} datastrips with missing AUX", missingAux.size());

        if (!CollectionUtils.isEmpty(missingAux)) {
            missingAux.forEach(this::updateAvailableAux);
            datastripService.updateAll(missingAux);
        }

        log.info("Finished updating AUX availability for all waiting datastrips");
    }

    @Transactional
    public void updateNotReady() {

        log.info("Updating ready and failed status for all Datastrips not yet ready");

        final List<Datastrip> datastrips = getNotReady();

        log.debug("Found {} datastrips not ready", datastrips.size());

        if (!CollectionUtils.isEmpty(datastrips)) {
            datastrips.forEach(datastrip -> {
                datastrip.setReady(datastrip.allAuxAvailable());
                if (datastrip.isReady()) {
                    log.info("Datastrip {} is now ready", datastrip.getName());
                }
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
