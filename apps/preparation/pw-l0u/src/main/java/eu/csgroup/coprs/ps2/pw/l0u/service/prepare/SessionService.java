package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.mongo.exception.MongoDBException;
import eu.csgroup.coprs.ps2.pw.l0u.model.AuxValue;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.model.SessionEntity;
import eu.csgroup.coprs.ps2.pw.l0u.model.SessionMapper;
import eu.csgroup.coprs.ps2.pw.l0u.repository.SessionEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SessionService {

    private final SessionEntityRepository sessionEntityRepository;
    private final SessionMapper sessionMapper;

    public SessionService(SessionEntityRepository sessionEntityRepository, SessionMapper sessionMapper) {
        this.sessionEntityRepository = sessionEntityRepository;
        this.sessionMapper = sessionMapper;
    }

    @Transactional
    public Session create(String sessionName, Instant startTime, Instant stopTime, String satellite, String stationCode) {

        log.info("Creating Session: {}", sessionName);

        if (sessionEntityRepository.existsById(sessionName)) {
            throw new MongoDBException("Session already exists with name: " + sessionName);
        }

        SessionEntity sessionEntity = new SessionEntity()
                .setName(sessionName)
                .setCreationDate(Instant.now())
                .setStartTime(startTime)
                .setStopTime(stopTime)
                .setSatellite(satellite)
                .setStationCode(stationCode)
                .setAvailableByAux(
                        Arrays.stream(AuxValue.values())
                                .map(AuxValue::getAuxProductType)
                                .distinct()
                                .collect(Collectors.toMap(Enum::name, o -> false))
                );

        sessionEntityRepository.save(sessionEntity);

        return sessionMapper.toSession(sessionEntity);
    }

    @Transactional
    public boolean exists(String sessionName) {
        return sessionEntityRepository.existsById(sessionName);
    }

    @Transactional
    public Session read(String sessionName) {
        log.debug("Retrieving session: {}", sessionName);
        return sessionMapper.toSession(readEntity(sessionName));
    }

    @Transactional
    public List<Session> readAll(boolean rawComplete, boolean ready, boolean failed, boolean jobOrderCreated) {
        final List<Session> sessions = sessionEntityRepository.findAllByRawCompleteAndReadyAndFailedAndJobOrderCreated(rawComplete, ready, failed, jobOrderCreated)
                .stream()
                .map(sessionMapper::toSession)
                .toList();
        log.debug("Retrieving multiple sessions ({})", sessions.size());
        return sessions;
    }

    @Transactional
    public List<Session> readAll(boolean ready, boolean failed, boolean jobOrderCreated) {
        final List<Session> sessions = sessionEntityRepository.findAllByReadyAndFailedAndJobOrderCreated(ready, failed, jobOrderCreated)
                .stream()
                .map(sessionMapper::toSession)
                .toList();
        log.debug("Retrieving multiple sessions ({})", sessions.size());
        return sessions;
    }

    @Transactional
    public Session update(Session session) {

        log.info("Updating session: {}", session.getName());

        SessionEntity sessionEntity = readEntity(session.getName());
        SessionEntity updatedSessionEntity = sessionMapper.toSessionEntity(session);

        updateEntity(sessionEntity, updatedSessionEntity);

        return sessionMapper.toSession(sessionEntityRepository.save(sessionEntity));
    }

    @Transactional
    public void updateAll(List<Session> sessionList) {

        log.info("Updating multiple sessions ({})", sessionList.size());

        final List<SessionEntity> updatedSessionEntities = sessionList.stream().map(sessionMapper::toSessionEntity).toList();

        final List<SessionEntity> sessionEntities = updatedSessionEntities.stream()
                .map(updatedSessionEntity -> {
                    final SessionEntity sessionEntity = readEntity(updatedSessionEntity.getName());
                    updateEntity(sessionEntity, updatedSessionEntity);
                    return sessionEntity;
                })
                .toList();

        sessionEntityRepository.saveAll(sessionEntities);
    }

    @Transactional
    public void delete(String sessionName) {
        log.info("Deleting session: {}", sessionName);
        SessionEntity sessionEntity = readEntity(sessionName);
        sessionEntityRepository.delete(sessionEntity);
    }

    private SessionEntity readEntity(String sessionName) {
        return sessionEntityRepository.findById(sessionName)
                .orElseThrow(() -> new MongoDBException("Session not found: " + sessionName));
    }

    private void updateEntity(SessionEntity sessionEntity, SessionEntity updatedSessionEntity) {
        sessionEntity.setRawComplete(updatedSessionEntity.isRawComplete())
                .setAvailableByAux(updatedSessionEntity.getAvailableByAux())
                .setReady(updatedSessionEntity.isReady())
                .setJobOrderCreated(updatedSessionEntity.isJobOrderCreated());
    }

}
