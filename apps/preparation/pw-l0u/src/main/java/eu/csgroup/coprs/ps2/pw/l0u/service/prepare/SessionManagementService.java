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

package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.catalog.ProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.utils.CatalogUtils;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemManagementService;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.model.SessionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Slf4j
@Service
@Transactional
public class SessionManagementService extends PWItemManagementService<Session, SessionEntity, SessionService> {

    private static final int TOTAL_SESSION_ENTRIES = 2;

    public SessionManagementService(CatalogService catalogService, SessionService itemService) {
        super(catalogService, itemService);
    }

    @Override
    public List<Session> getMissingAux() {
        return itemService.readAll(true, false, false);
    }

    @Override
    public boolean isReady(Session item) {
        return item.isRawComplete() && item.allAuxAvailable();
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
                                        .map(CatalogUtils::getT0PdgsDate)
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

}
