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


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static eu.csgroup.coprs.ps2.core.common.model.processing.Level.*;


@Getter
@AllArgsConstructor
public enum DatatakeType {

    NOBS(List.of(L0U, L0C, L1B, L1C, L2A)),
    VIC(List.of(L0U, L0C, L1B, L1C)),
    RAW(List.of(L0U, L0C, L1A, L1B, L1C, L2A)),
    DASC(List.of(L0U, L0C, L1A)),
    ABSR(List.of(L0U, L0C, L1A)),
    EOBS(List.of(L0U, L0C, L1A)),
    DEFAULT(List.of(L0U, L0C));

    private final List<Level> levelList;

}
