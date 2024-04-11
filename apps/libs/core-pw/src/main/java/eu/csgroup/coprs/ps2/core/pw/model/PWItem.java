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

package eu.csgroup.coprs.ps2.core.pw.model;

import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public abstract class PWItem {

    protected String name;

    protected String satellite;
    protected String stationCode;

    protected Instant createdDate;
    protected Instant lastModifiedDate;

    protected Instant startTime;
    protected Instant stopTime;

    protected Instant t0PdgsDate;

    protected Map<String, Boolean> availableByAux;

    protected boolean ready;
    protected boolean jobOrderCreated;

    public boolean allAuxAvailable() {
        return availableByAux.values().stream().allMatch(Boolean::booleanValue);
    }

    public String getSatelliteName() {
        return Mission.S2.getValue() + satellite;
    }

}
