/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemService;
import eu.csgroup.coprs.ps2.pw.l2.model.L2AuxFile;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripEntity;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripMapper;
import eu.csgroup.coprs.ps2.pw.l2.repository.L2DatastripEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class L2DatastripService extends PWItemService<L2Datastrip, L2DatastripEntity> {

    public L2DatastripService(L2DatastripEntityRepository datastripEntityRepository, L2DatastripMapper datastripMapper) {
        super(datastripEntityRepository, datastripMapper);
    }


    public L2Datastrip create(String datastripName, String folder, Pair<Instant, Instant> datastripTimes, String satellite, Instant t0PdgsDate, List<String> tlList) {

        log.info("Creating Datastrip: {}", datastripName);

        if (itemRepository.existsById(datastripName)) {
            throw new MongoDBException("Datastrip already exists with name: " + datastripName);
        }

        L2DatastripEntity datastripEntity = new L2DatastripEntity();
        datastripEntity
                .setFolder(folder)
                .setAvailableByTL(
                        tlList.stream().collect(Collectors.toMap(Function.identity(), o -> false))
                )
                .setName(datastripName)
                .setStartTime(datastripTimes.getLeft())
                .setStopTime(datastripTimes.getRight())
                .setSatellite(satellite)
                .setT0PdgsDate(t0PdgsDate)
                .setAvailableByAux(
                        Arrays.stream(L2AuxFile.values()).collect(Collectors.toMap(Enum::name, o -> false))
                );

        itemRepository.save(datastripEntity);

        return itemMapper.toItem(datastripEntity);
    }

    public List<L2Datastrip> readAll(boolean tlComplete, boolean ready, boolean jobOrderCreated) {
        final List<L2Datastrip> datastrips = toItems(
                ((L2DatastripEntityRepository) itemRepository).findAllByTlCompleteAndReadyAndJobOrderCreated(tlComplete, ready, jobOrderCreated)
        );
        log.debug(RETRIEVING_MULTIPLE_ITEMS, datastrips.size());
        return datastrips;
    }

    @Override
    protected void updateEntity(L2DatastripEntity datastripEntity, L2DatastripEntity updatedDatastripEntity) {
        datastripEntity
                .setAvailableByTL(updatedDatastripEntity.getAvailableByTL())
                .setTlComplete(updatedDatastripEntity.isTlComplete())
                .setAvailableByAux(updatedDatastripEntity.getAvailableByAux())
                .setReady(updatedDatastripEntity.isReady())
                .setJobOrderCreated(updatedDatastripEntity.isJobOrderCreated());
    }

}
