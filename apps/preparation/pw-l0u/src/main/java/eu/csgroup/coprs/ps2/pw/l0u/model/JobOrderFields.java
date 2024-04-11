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

package eu.csgroup.coprs.ps2.pw.l0u.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobOrderFields {

    // Hard coded in template:
    // WORKING = scenario folder
    // CADU = input folder
    //
    // From Session data
    // MissionID = Mission + Satellite ("S2" + SESSION)
    // DownlinkTime = startTime -- FORMAT = 20191003T131732
    // AcquisitionStart = startTime -- FORMAT = 20191003T131732
    // ACQUI_STATION = stationCode (SESSION)
    // Processing Station = JobParameters.PROC_STATION
    //
    // From AUX Values:
    // TheoreticalLinePeriod
    // MinimumPacketLength
    // GPSToTAI
    // TAIToUTC
    // OrbitOffset
    // CyclicOrbitOffset

    MISSION_ID("__PH_MisionID"),
    DOWNLINK_TIME("__PH_DownlinkTime"),
    ACQUISITION_START("__PH_AcquisitionStart"),
    PROCESSING_STATION("__PH_Processing_Station"),
    ACQUISITION_STATION("__PH_Acquisition_Station"),

    THEORETICAL_LINE_PERIOD("__PH_theoretical_line_period"),
    MINIMUM_PACKET_LENGTH("__PH_minimum_packet_length"),
    GPS_TO_TAI("__PH_gps_to_tai"),
    TAI_TO_UTC("__PH_tai_to_utc"),
    ORBIT_OFFSET("__PH_orbit_offset"),
    CYCLIC_ORBIT_OFFSET("__PH_cyclic_orbit_offset");

    private final String placeholder;

}
