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

package eu.csgroup.coprs.ps2.core.common.model.trace.missing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MissingOutputProductType {

    L0_GR("MSI_L0__GR"),
    L0_DS("MSI_L0__DS"),
    L1A_GR("MSI_L1A_GR"),
    L1A_DS("MSI_L1A_DS"),
    L1B_GR("MSI_L1B_GR"),
    L1B_DS("MSI_L1B_DS"),
    L1C_DS("MSI_L1C_DS"),
    L1C_TL("MSI_L1C_TL"),
    L1C_TC("MSI_L1C_TC"),
    L2A_DS("MSI_L2A_DS"),
    L2A_TL("MSI_L2A_TL"),
    L2A_TC("MSI_L2A_TC"),
    HKTM("PRD_HKTM"),
    SAD("AUX_SADATA");

    private final String type;

}
