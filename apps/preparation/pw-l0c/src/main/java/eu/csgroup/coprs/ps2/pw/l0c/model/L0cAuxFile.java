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

package eu.csgroup.coprs.ps2.pw.l0c.model;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFile;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFolder;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cAuxFile implements AuxFile {

    AUX_UT1UTC(AuxProductType.AUX_UT1UTC, AuxFolder.S2IPF_IERS),
    GIP_ATMIMA(AuxProductType.GIP_ATMIMA, AuxFolder.S2IPF_GIPP),
    GIP_ATMSAD(AuxProductType.GIP_ATMSAD, AuxFolder.S2IPF_GIPP),
    GIP_BLINDP(AuxProductType.GIP_BLINDP, AuxFolder.S2IPF_GIPP),
    GIP_CLOINV(AuxProductType.GIP_CLOINV, AuxFolder.S2IPF_GIPP),
    GIP_DATATI(AuxProductType.GIP_DATATI, AuxFolder.S2IPF_GIPP),
    GIP_INVLOC(AuxProductType.GIP_INVLOC, AuxFolder.S2IPF_GIPP),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, AuxFolder.S2IPF_GIPP),
    GIP_LREXTR(AuxProductType.GIP_LREXTR, AuxFolder.S2IPF_GIPP),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, AuxFolder.S2IPF_GIPP),
    GIP_PROBAS(AuxProductType.GIP_PROBAS, AuxFolder.S2IPF_GIPP),
    GIP_R2ABCA(AuxProductType.GIP_R2ABCA, AuxFolder.S2IPF_GIPP),
    GIP_SPAMOD(AuxProductType.GIP_SPAMOD, AuxFolder.S2IPF_GIPP),
    GIP_VIEDIR(AuxProductType.GIP_VIEDIR, AuxFolder.S2IPF_GIPP);

    private final AuxProductType auxProductType;
    private final AuxFolder folder;

}
