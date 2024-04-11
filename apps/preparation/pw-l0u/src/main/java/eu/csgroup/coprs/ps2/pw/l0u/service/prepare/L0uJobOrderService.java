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

import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.common.utils.TemplateUtils;
import eu.csgroup.coprs.ps2.pw.l0u.model.AuxValue;
import eu.csgroup.coprs.ps2.pw.l0u.model.JobOrderFields;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class L0uJobOrderService {

    private static final String JOB_ORDER_TEMPLATE_FOLDER = "templates";

    private final L0uAuxService auxService;

    public L0uJobOrderService(L0uAuxService auxService) {
        this.auxService = auxService;
    }


    public Map<String, String> create(Session session) {

        log.info("Creating Job Orders for session: {}", session.getName());

        final Map<AuxValue, String> infoByAuxValue = auxService.getValues(session.getSatellite(), session.getStartTime(), session.getStopTime());

        final Map<String, String> values = new HashMap<>();

        String startTime = DateUtils.toShortDate(session.getStartTime());

        values.put(JobOrderFields.MISSION_ID.getPlaceholder(), "S2" + session.getSatellite());
        values.put(JobOrderFields.DOWNLINK_TIME.getPlaceholder(), startTime);
        values.put(JobOrderFields.ACQUISITION_START.getPlaceholder(), startTime);
        values.put(JobOrderFields.PROCESSING_STATION.getPlaceholder(), JobParameters.PROC_STATION);
        values.put(JobOrderFields.ACQUISITION_STATION.getPlaceholder(), session.getStationCode());
        values.put(JobOrderFields.THEORETICAL_LINE_PERIOD.getPlaceholder(), infoByAuxValue.get(AuxValue.THEORETICAL_LINE_PERIOD));
        values.put(JobOrderFields.MINIMUM_PACKET_LENGTH.getPlaceholder(), infoByAuxValue.get(AuxValue.SP_MIN_SIZE));
        values.put(JobOrderFields.GPS_TO_TAI.getPlaceholder(), infoByAuxValue.get(AuxValue.GPS_TIME_TAI));
        values.put(JobOrderFields.TAI_TO_UTC.getPlaceholder(), infoByAuxValue.get(AuxValue.TAI_TO_UTC));
        values.put(JobOrderFields.ORBIT_OFFSET.getPlaceholder(), infoByAuxValue.get(AuxValue.ORBIT_OFFSET));
        values.put(JobOrderFields.CYCLIC_ORBIT_OFFSET.getPlaceholder(), infoByAuxValue.get(AuxValue.TOTAL_ORBIT));

        Map<String, String> jobOrders = TemplateUtils.fillTemplates(JOB_ORDER_TEMPLATE_FOLDER, values);

        log.info("Finished creating Job Orders for session: {}", session.getName());

        return jobOrders;
    }

}
