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

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum ProductFamily {
    EDRS_SESSION,
    S2_L0_GR,
    S2_L0_DS,
    S2_L1A_GR,
    S2_L1A_DS,
    S2_L1B_GR,
    S2_L1B_DS,
    S2_L1C_TL,
    S2_L1C_DS,
    S2_L1C_TC,
    S2_L2A_TL,
    S2_L2A_DS,
    S2_L2A_TC,
    S2_AUX,
    S2_SAD,
    S2_HKTM,
    @JsonEnumDefaultValue
    UNKNOWN

}
