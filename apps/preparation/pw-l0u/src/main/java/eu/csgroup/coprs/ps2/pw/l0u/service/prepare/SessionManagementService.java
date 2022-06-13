package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.catalog.model.AuxProductType;
import eu.csgroup.coprs.ps2.core.catalog.model.ProductType;
import eu.csgroup.coprs.ps2.core.catalog.model.SessionCatalogData;
import eu.csgroup.coprs.ps2.core.catalog.service.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.SessionParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
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
public class SessionManagementService {

    private static final int TOTAL_SESSION_ENTRIES = 2;

    private final SessionService sessionService;
    private final CatalogService catalogService;

    public SessionManagementService(SessionService sessionService, CatalogService catalogService) {
        this.sessionService = sessionService;
        this.catalogService = catalogService;
    }

    @Transactional
    public List<Session> getReady() {
        return sessionService.readAll(true, false, false);
    }

    @Transactional
    public List<Session> getNotReady() {
        return sessionService.readAll(false, false, false);
    }

    @Transactional
    public List<Session> getMissingAux() {
        return sessionService.readAll(true, false, false, false);
    }

    @Transactional
    public void create(String sessionName) {
        if (!sessionService.exists(sessionName)) {
            catalogService.retrieveSessionData(sessionName)
                    .stream()
                    .filter(sessionCatalogData -> ProductType.SESSION.name().equals(sessionCatalogData.getProductType()))
                    .findAny()
                    .ifPresent(sessionCatalogData -> {
                        Instant start = DateUtils.toInstant(sessionCatalogData.getStartTime());
                        Instant stop = DateUtils.toInstant(sessionCatalogData.getStopTime());
                        sessionService.create(sessionName, start, stop, sessionCatalogData.getSatelliteId(), sessionCatalogData.getStationCode());
                    });
        }
        updateRawComplete(sessionName);
    }

    @Transactional
    public void updateRawComplete(String sessionName) {

        if (sessionService.exists(sessionName)) {

            final Session session = sessionService.read(sessionName);

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
                    sessionService.update(session);
                }

                log.info("Finished checking RAW files availability for session {}", sessionName);
            }
        }
    }

    @Transactional
    public void updateAvailableAux() {

        log.info("Updating AUX availability for all sessions");

        final List<Session> missingAux = getMissingAux();

        log.debug("Found {} sessions with missing AUX", missingAux.size());

        if (!CollectionUtils.isEmpty(missingAux)) {
            missingAux.forEach(this::updateAvailableAux);
            sessionService.updateAll(missingAux);
        }

        log.info("Finished updating AUX availability for all sessions");
    }

    @Transactional
    public void updateNotReady() {

        log.info("Updating ready and failed status for all sessions not yet ready");

        final List<Session> sessions = getNotReady();

        log.debug("Found {} sessions not ready", sessions.size());

        if (!CollectionUtils.isEmpty(sessions)) {
            sessions.forEach(session -> {
                updateReadyStatus(session);
                updateFailedStatus(session);
            });
            sessionService.updateAll(sessions);
        }

        log.info("Finished updating ready and failed status for all sessions not yet ready");
    }

    @Transactional
    public void setJobOrderCreated(List<Session> sessionList) {
        sessionList.forEach(session -> session.setJobOrderCreated(true));
        sessionService.updateAll(sessionList);
    }

    private void updateReadyStatus(Session session) {
        boolean ready = session.isRawComplete() && session.allAuxAvailable();
        session.setReady(ready);
    }

    private void updateFailedStatus(Session session) {
        // TODO check this
        if (!session.isReady()) {
            final Instant creationDate = session.getCreationDate();
            if (Duration.between(creationDate, Instant.now()).toHours() > SessionParameters.FAILED_DELAY) {
                session.setFailed(true);
            }
        }
    }

    private void updateAvailableAux(Session session) {

        log.debug("Updating AUX availability for session {}", session.getName());

        if (!session.allAuxAvailable()) {

            session.getAvailableByAux()
                    .entrySet()
                    .forEach(entry -> {

                        AuxProductType auxProductType = AuxProductType.valueOf(entry.getKey());

                        if (Boolean.FALSE.equals(entry.getValue())) {

                            catalogService.retrieveLatestAuxData(
                                            auxProductType,
                                            session.getSatellite(),
                                            session.getStartTime(),
                                            session.getStopTime())
                                    .ifPresent(auxCatalogData -> entry.setValue(Boolean.TRUE));
                        }
                    });
        }

        log.debug("Finished updating AUX availability for session {}", session.getName());
    }

}
