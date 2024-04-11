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

package eu.csgroup.coprs.ps2.core.common.model.processing;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Timeliness {

    EMPTY(""),
    FAST24("FAST"),
    OPERATOR_DEMAND("OPERATOR-DEMAND"),
    NRT("NRT"),
    NTC("NTC"),
    PT("PT"),
    STC("STC"),
    S2_SESSION("S2_SESSION"),
    S2_L0("S2_L0"),
    S2_L1("S2_L1"),
    S2_L2("S2_L2");

    private String value;

    Timeliness(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
