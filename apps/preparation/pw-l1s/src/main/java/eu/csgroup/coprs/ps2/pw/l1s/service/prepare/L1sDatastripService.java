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

package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.pw.exception.MongoDBException;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemService;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sAuxFile;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripEntity;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripMapper;
import eu.csgroup.coprs.ps2.pw.l1s.repository.L1sDatastripEntityRepository;
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
public class L1sDatastripService extends PWItemService<L1sDatastrip, L1sDatastripEntity> {

    public L1sDatastripService(L1sDatastripEntityRepository datastripEntityRepository, L1sDatastripMapper datastripMapper) {
        super(datastripEntityRepository, datastripMapper);
    }


    public L1sDatastrip create(String datastripName, String folder, Pair<Instant, Instant> datastripTimes, String satellite, Instant t0PdgsDate, DatatakeType datatakeType,
            List<String> grList
    ) {

        log.info("Creating Datastrip: {}", datastripName);

        if (itemRepository.existsById(datastripName)) {
            throw new MongoDBException("Datastrip already exists with name: " + datastripName);
        }

        L1sDatastripEntity datastripEntity = new L1sDatastripEntity();
        datastripEntity
                .setFolder(folder)
                .setDatatakeType(datatakeType)
                .setAvailableByGR(
                        grList.stream().collect(Collectors.toMap(Function.identity(), o -> false))
                )
                .setName(datastripName)
                .setStartTime(datastripTimes.getLeft())
                .setStopTime(datastripTimes.getRight())
                .setSatellite(satellite)
                .setT0PdgsDate(t0PdgsDate)
                .setAvailableByAux(
                        Arrays.stream(L1sAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false))
                );

        itemRepository.save(datastripEntity);

        return itemMapper.toItem(datastripEntity);
    }

    public List<L1sDatastrip> readAll(boolean grComplete, boolean ready, boolean jobOrderCreated) {
        final List<L1sDatastrip> datastrips = toItems(
                ((L1sDatastripEntityRepository) itemRepository).findAllByGrCompleteAndReadyAndJobOrderCreated(grComplete, ready, jobOrderCreated)
        );
        log.debug(RETRIEVING_MULTIPLE_ITEMS, datastrips.size());
        return datastrips;
    }

    @Override
    protected void updateEntity(L1sDatastripEntity datastripEntity, L1sDatastripEntity updatedDatastripEntity) {
        datastripEntity
                .setAvailableByGR(updatedDatastripEntity.getAvailableByGR())
                .setGrComplete(updatedDatastripEntity.isGrComplete())
                .setAvailableByAux(updatedDatastripEntity.getAvailableByAux())
                .setReady(updatedDatastripEntity.isReady())
                .setJobOrderCreated(updatedDatastripEntity.isJobOrderCreated());
    }

}
