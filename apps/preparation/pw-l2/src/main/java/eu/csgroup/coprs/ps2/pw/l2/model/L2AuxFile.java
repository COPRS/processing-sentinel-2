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

package eu.csgroup.coprs.ps2.pw.l2.model;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFile;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFolder;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L2AuxFile implements AuxFile {

    // Mandatory
    GIP_PROBA2(AuxProductType.GIP_PROBA2, AuxFolder.S2IPF_GIPP, false),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, AuxFolder.S2IPF_GIPP, false),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, AuxFolder.S2IPF_GIPP, false),
    // Optional
    GIP_L2ACAC(AuxProductType.GIP_L2ACAC, AuxFolder.S2IPF_GIPP, true),
    GIP_L2ACFG(AuxProductType.GIP_L2ACFG, AuxFolder.S2IPF_GIPP, true),
    GIP_L2ACSC(AuxProductType.GIP_L2ACSC, AuxFolder.S2IPF_GIPP, true);

    private final AuxProductType auxProductType;
    private final AuxFolder folder;
    private final boolean optional;

}
