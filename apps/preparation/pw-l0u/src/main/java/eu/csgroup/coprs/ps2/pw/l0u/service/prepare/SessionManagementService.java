package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.catalog.ProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.service.pw.PWItemManagementService;
import eu.csgroup.coprs.ps2.core.common.utils.CatalogUtils;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.pw.l0u.config.L0uPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Slf4j
@Service
@Transactional
public class SessionManagementService extends PWItemManagementService<Session, SessionService> {

    private static final int TOTAL_SESSION_ENTRIES = 2;
    private static final long DELETION_GRACE_PERIOD = 60L;
    private static final String TO_PDGS_DATE_PROPERTY = "t0_pdgs_date";

    public SessionManagementService(CatalogService catalogService, SessionService itemService, L0uPreparationProperties l0uPreparationProperties) {
        super(catalogService, itemService, l0uPreparationProperties);
    }

    @Override
    public List<Session> getReady() {
        return itemService.readAll(true, false, false);
    }

    @Override
    public List<Session> getDeletable() {
        final Instant now = Instant.now();
        return itemService.readAllOr(true, true).stream()
                .filter(session -> Duration.between(session.getLastModifiedDate(), now).getSeconds() > DELETION_GRACE_PERIOD)
                .toList();
    }

    @Override
    public List<Session> getWaiting() {
        return itemService.readAll(false, false);
    }

    @Override
    public void updateAvailableAux() {

        log.info("Updating AUX availability for all waiting sessions");

        final List<Session> missingAux = getMissingAux();

        log.debug("Found {} sessions with missing AUX", missingAux.size());

        if (!CollectionUtils.isEmpty(missingAux)) {
            missingAux.forEach(this::updateAvailableAux);
            itemService.updateAll(missingAux);
        }

        log.info("Finished updating AUX availability for all waiting sessions");
    }

    @Override
    public void updateNotReady() {

        log.info("Updating ready status for all waiting sessions");

        final List<Session> sessions = getNotReady();

        log.debug("Found {} sessions not ready", sessions.size());

        if (!CollectionUtils.isEmpty(sessions)) {
            sessions.forEach(session -> {
                boolean ready = session.isRawComplete() && session.allAuxAvailable();
                session.setReady(ready);
                if (ready) {
                    log.info("Session {} is now ready", session.getName());
                }
            });
            itemService.updateAll(sessions);
        }

        log.info("Finished updating ready status for all waiting sessions");
    }

    @Override
    public void setJobOrderCreated(List<Session> sessionList) {
        sessionList.forEach(session -> session.setJobOrderCreated(true));
        itemService.updateAll(sessionList);
    }

    public void create(String sessionName, Instant t0PdgsDate) {

        if (!itemService.exists(sessionName)) {
            catalogService.retrieveSessionData(sessionName)
                    .stream()
                    .filter(sessionCatalogData -> ProductType.SESSION.name().equals(sessionCatalogData.getProductType()))
                    .findAny()
                    .ifPresent(sessionCatalogData -> {
                        Instant start = DateUtils.toInstant(sessionCatalogData.getStartTime());
                        Instant stop = DateUtils.toInstant(sessionCatalogData.getStopTime());
                        itemService.create(sessionName, start, stop, sessionCatalogData.getSatelliteId(), sessionCatalogData.getStationCode(), t0PdgsDate);
                    });
        }

        updateRawComplete(sessionName);
    }

    public void updateRawComplete(String sessionName) {

        if (itemService.exists(sessionName)) {

            final Session session = itemService.read(sessionName);

            if (!session.isRawComplete()) {

                log.info("Checking RAW files availability for session {}", sessionName);

                final List<SessionCatalogData> sessionCatalogDataList = catalogService.retrieveSessionData(session.getName());

                final boolean allSessionAvailable =
                        TOTAL_SESSION_ENTRIES == sessionCatalogDataList.stream()
                                .filter(sessionCatalogData -> ProductType.SESSION.name().equals(sessionCatalogData.getProductType()))
                                .count();

                if (allSessionAvailable) {

                    final long rawCount = sessionCatalogDataList.stream()
                            .filter(sessionCatalogData -> ProductType.RAW.name().equals(sessionCatalogData.getProductType()))
                            .count();

                    final long totalRawCount = sessionCatalogDataList.stream()
                            .filter(sessionCatalogData -> ProductType.SESSION.name().equals(sessionCatalogData.getProductType()))
                            .mapToLong(sessionCatalogData -> sessionCatalogData.getRawNames().size())
                            .sum();

                    session.setRawComplete(rawCount == totalRawCount);

                    if (session.isRawComplete()) {
                        session.setT0PdgsDate(
                                sessionCatalogDataList.stream()
                                        .map(sessionCatalogData -> CatalogUtils.getAdditionalProperty(sessionCatalogData, TO_PDGS_DATE_PROPERTY, Instant.class))
                                        .max(Instant::compareTo)
                                        .orElse(session.getT0PdgsDate())
                        );
                    }

                    itemService.update(session);
                }

                log.info("Finished checking RAW files availability for session {}", sessionName);
            }
        }
    }

    private List<Session> getNotReady() {
        return itemService.readAll(false, false, false);
    }

    private List<Session> getMissingAux() {
        return itemService.readAll(true, false, false, false);
    }

}
