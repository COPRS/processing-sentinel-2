package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.mongo.exception.MongoDBException;
import eu.csgroup.coprs.ps2.pw.l0c.model.AuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.DatastripEntity;
import eu.csgroup.coprs.ps2.pw.l0c.model.DatastripMapper;
import eu.csgroup.coprs.ps2.pw.l0c.repository.DatastripEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DatastripService {

    private final DatastripEntityRepository datastripEntityRepository;
    private final DatastripMapper datastripMapper;

    public DatastripService(DatastripEntityRepository datastripEntityRepository, DatastripMapper datastripMapper) {
        this.datastripEntityRepository = datastripEntityRepository;
        this.datastripMapper = datastripMapper;
    }

    @Transactional
    public Datastrip create(String datastripName, Instant startTime, Instant stopTime, String satellite, String stationCode) {

        log.info("Creating Datastrip: {}", datastripName);

        if (datastripEntityRepository.existsById(datastripName)) {
            throw new MongoDBException("Datastrip already exists with name: " + datastripName);
        }

        DatastripEntity sessionEntity = new DatastripEntity()
                .setName(datastripName)
                .setCreationDate(Instant.now())
                .setStartTime(startTime)
                .setStopTime(stopTime)
                .setSatellite(satellite)
                .setStationCode(stationCode)
                .setAvailableByAux(
                        Arrays.stream(AuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false))
                );

        datastripEntityRepository.save(sessionEntity);

        return datastripMapper.toDatastrip(sessionEntity);
    }

    @Transactional
    public boolean exists(String datastripName) {
        return datastripEntityRepository.existsById(datastripName);
    }

    @Transactional
    public Datastrip read(String datastripName) {
        log.debug("Retrieving Datastrip: {}", datastripName);
        return datastripMapper.toDatastrip(readEntity(datastripName));
    }

    @Transactional
    public List<Datastrip> readAll(boolean ready, boolean failed, boolean jobOrderCreated) {
        return datastripEntityRepository.findAllByReadyAndFailedAndJobOrderCreated(ready, failed, jobOrderCreated)
                .stream()
                .map(datastripMapper::toDatastrip)
                .toList();
    }

    @Transactional
    public Datastrip update(Datastrip datastrip) {

        log.info("Updating Datastrip: {}", datastrip.getName());

        DatastripEntity datastripEntity = readEntity(datastrip.getName());
        DatastripEntity updatedDatastripEntity = datastripMapper.toDatastripEntity(datastrip);

        updateEntity(datastripEntity, updatedDatastripEntity);

        return datastripMapper.toDatastrip(datastripEntityRepository.save(datastripEntity));
    }

    @Transactional
    public void updateAll(List<Datastrip> datastripList) {

        log.info("Updating multiple Datastrips");

        final List<DatastripEntity> updatedDatastripEntities = datastripList.stream().map(datastripMapper::toDatastripEntity).toList();

        final List<DatastripEntity> datastripEntities = updatedDatastripEntities.stream()
                .map(updatedDatastripEntity -> {
                    final DatastripEntity datastripEntity = readEntity(updatedDatastripEntity.getName());
                    updateEntity(datastripEntity, updatedDatastripEntity);
                    return datastripEntity;
                })
                .toList();

        datastripEntityRepository.saveAll(datastripEntities);
    }

    @Transactional
    public void delete(String datastripName) {
        log.info("Deleting Datastrip: {}", datastripName);
        DatastripEntity datastripEntity = readEntity(datastripName);
        datastripEntityRepository.delete(datastripEntity);
    }

    private DatastripEntity readEntity(String datastripName) {
        return datastripEntityRepository.findById(datastripName)
                .orElseThrow(() -> new MongoDBException("Datastrip not found: " + datastripName));
    }

    private void updateEntity(DatastripEntity datastripEntity, DatastripEntity updatedDatastripEntity) {
        datastripEntity.setAvailableByAux(updatedDatastripEntity.getAvailableByAux())
                .setReady(updatedDatastripEntity.isReady())
                .setJobOrderCreated(updatedDatastripEntity.isJobOrderCreated());
    }

}
