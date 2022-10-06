package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemService;
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
@Transactional
public class SessionService extends PWItemService<Session, SessionEntity> {

    public SessionService(SessionEntityRepository sessionEntityRepository, SessionMapper sessionMapper) {
        super(sessionEntityRepository, sessionMapper);
    }


    public Session create(String sessionName, Instant startTime, Instant stopTime, String satellite, String stationCode, Instant t0PdgsDate) {

        log.info("Creating Session: {}", sessionName);

        if (itemRepository.existsById(sessionName)) {
            throw new MongoDBException("Session already exists with name: " + sessionName);
        }

        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity
                .setName(sessionName)
                .setStartTime(startTime)
                .setStopTime(stopTime)
                .setSatellite(satellite)
                .setStationCode(stationCode)
                .setT0PdgsDate(t0PdgsDate)
                .setAvailableByAux(
                        Arrays.stream(AuxValue.values())
                                .map(AuxValue::getAuxProductType)
                                .distinct()
                                .collect(Collectors.toMap(Enum::name, o -> false))
                );

        return itemMapper.toItem(itemRepository.save(sessionEntity));
    }

    public List<Session> readAll(boolean rawComplete, boolean ready, boolean jobOrderCreated) {
        final List<Session> sessions = toItems(
                ((SessionEntityRepository) itemRepository).findAllByRawCompleteAndReadyAndJobOrderCreated(rawComplete, ready, jobOrderCreated)
        );
        log.debug(RETRIEVING_MULTIPLE_ITEMS, sessions.size());
        return sessions;
    }

    @Override
    protected void updateEntity(SessionEntity sessionEntity, SessionEntity updatedSessionEntity) {
        sessionEntity
                .setRawComplete(updatedSessionEntity.isRawComplete())
                .setAvailableByAux(updatedSessionEntity.getAvailableByAux())
                .setReady(updatedSessionEntity.isReady())
                .setJobOrderCreated(updatedSessionEntity.isJobOrderCreated())
                .setT0PdgsDate(updatedSessionEntity.getT0PdgsDate());
    }

}
