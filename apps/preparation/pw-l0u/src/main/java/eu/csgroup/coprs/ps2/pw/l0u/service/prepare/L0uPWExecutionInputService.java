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

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.pw.service.PWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class L0uPWExecutionInputService implements PWExecutionInputService<L0uExecutionInput, Session> {

    private final CatalogService catalogService;
    private final L0uJobOrderService jobOrderService;
    private final ObsBucketProperties bucketProperties;

    public L0uPWExecutionInputService(CatalogService catalogService, L0uJobOrderService jobOrderService, ObsBucketProperties bucketProperties) {
        this.catalogService = catalogService;
        this.jobOrderService = jobOrderService;
        this.bucketProperties = bucketProperties;
    }

    @Override
    public List<L0uExecutionInput> create(List<Session> sessionList) {

        log.info("Creating output payload for all ready sessions ({})", sessionList.size());

        final List<L0uExecutionInput> l0uExecutionInputs = sessionList.stream().map(this::create).toList();

        log.info("Finished creating output payload for all ready sessions ({})", sessionList.size());

        return l0uExecutionInputs;
    }

    private L0uExecutionInput create(Session session) {

        final String sessionName = session.getName();

        log.info("Creating output payload for session {}", sessionName);

        final L0uExecutionInput l0uExecutionInput = new L0uExecutionInput();
        l0uExecutionInput.setSession(sessionName)
                .setJobOrders(jobOrderService.create(session))
                .setFiles(getFileInfos(sessionName))
                .setSatellite(session.getSatellite())
                .setStation(session.getStationCode())
                .setStartTime(session.getStartTime())
                .setStopTime(session.getStopTime())
                .setT0PdgsDate(session.getT0PdgsDate());

        log.info("Finished creating output payload for session {}", sessionName);

        return l0uExecutionInput;
    }

    private Set<FileInfo> getFileInfos(String sessionName) {
        return catalogService.retrieveSessionData(sessionName)
                .stream()
                .map(sessionCatalogData -> new FileInfo()
                        .setBucket(bucketProperties.getSessionBucket())
                        .setKey(sessionCatalogData.getKeyObjectStorage())
                        .setProductFamily(ProductFamily.EDRS_SESSION)
                        .setSimpleFile(true))
                .collect(Collectors.toSet());
    }

}
