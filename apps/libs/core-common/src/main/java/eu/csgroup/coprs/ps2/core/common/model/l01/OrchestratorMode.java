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

package eu.csgroup.coprs.ps2.core.common.model.l01;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrchestratorMode {

    L0C("L0"),
    OLQC_L0DS("OLQC_L0DS"),
    OLQC_L0GR("OLQC_L0GR"),
    L1A("L1A"),
    L1B("L1B"),
    L1B_NO_GRI("L1BNoGRI"),
    L1A_FORMAT_GR("L1AFormatGR"),
    L1A_FORMAT_DS("L1AFormatDS"),
    OLQC_L1BDS("OLQC_L1BDS"),
    L1B_FORMAT_GR("L1BFormatGR"),
    OLQC_L1CDS("OLQC_L1CDS"),
    L1C_TILE("L1CTile"),
    OLQC_L1CTL("OLQC_L1CTL");

    private final String mode;

}
