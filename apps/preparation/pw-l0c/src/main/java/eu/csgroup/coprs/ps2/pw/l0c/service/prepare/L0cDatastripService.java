package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripEntity;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripMapper;
import eu.csgroup.coprs.ps2.pw.l0c.repository.L0cDatastripEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class L0cDatastripService extends PWItemService<L0cDatastrip, L0cDatastripEntity> {

    public L0cDatastripService(L0cDatastripEntityRepository datastripEntityRepository, L0cDatastripMapper datastripMapper) {
        super(datastripEntityRepository, datastripMapper);
    }


    public L0cDatastrip create(Path datastripPath, Instant startTime, Instant stopTime, String satellite, String stationCode, Instant t0PdgsDate) {

        final String datastripName = datastripPath.getFileName().toString();

        log.info("Creating Datastrip: {}", datastripName);

        if (itemRepository.existsById(datastripName)) {
            throw new MongoDBException("Datastrip already exists with name: " + datastripName);
        }

        L0cDatastripEntity datastripEntity = new L0cDatastripEntity();
        datastripEntity
                .setFolder(datastripPath.getParent().toString())
                .setDtFolder(datastripPath.getParent().getParent().toString())
                .setName(datastripName)
                .setStartTime(startTime)
                .setStopTime(stopTime)
                .setSatellite(satellite)
                .setStationCode(stationCode)
                .setT0PdgsDate(t0PdgsDate)
                .setAvailableByAux(
                        Arrays.stream(L0cAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false))
                );

        itemRepository.save(datastripEntity);

        return itemMapper.toItem(datastripEntity);
    }

    @Override
    protected void updateEntity(L0cDatastripEntity datastripEntity, L0cDatastripEntity updatedDatastripEntity) {
        datastripEntity
                .setAvailableByAux(updatedDatastripEntity.getAvailableByAux())
                .setReady(updatedDatastripEntity.isReady())
                .setJobOrderCreated(updatedDatastripEntity.isJobOrderCreated());
    }

}
