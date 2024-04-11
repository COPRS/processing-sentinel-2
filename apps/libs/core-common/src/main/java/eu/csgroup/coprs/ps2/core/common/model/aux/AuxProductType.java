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

package eu.csgroup.coprs.ps2.core.common.model.aux;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuxProductType {

    AUX_CAMSFO("", false),
    AUX_ECMWFD("", false),
    AUX_UT1UTC(".txt", false),
    GIP_ATMIMA(".xml", false),
    GIP_ATMSAD(".xml", false),
    GIP_BLINDP(".xml", false),
    GIP_CLOINV(".xml", false),
    GIP_CLOPAR(".xml", false),
    GIP_CONVER(".xml", false),
    GIP_DATATI(".xml", false),
    GIP_DECOMP(".xml", false),
    GIP_EARMOD(".xml", false),
    GIP_ECMWFP(".xml", false),
    GIP_G2PARA(".xml", false),
    GIP_G2PARE(".xml", false),
    GIP_GEOPAR(".xml", false),
    GIP_HRTPAR(".xml", false),
    GIP_INTDET(".xml", false),
    GIP_INVLOC(".xml", false),
    GIP_JP2KPA(".xml", false),
    GIP_L2ACAC(".xml", false),
    GIP_L2ACFG(".xml", false),
    GIP_L2ACSC(".xml", false),
    GIP_LREXTR(".xml", false),
    GIP_MASPAR(".xml", false),
    GIP_OLQCPA(".zip", false),
    GIP_PRDLOC(".xml", false),
    GIP_PROBA2(".xml", false),
    GIP_PROBAS(".xml", false),
    GIP_R2ABCA(".xml", false),
    GIP_R2BINN(".xml", false),
    GIP_R2CRCO(".xml", false),
    GIP_R2DECT(".xml", true),
    GIP_R2DEFI(".xml", true),
    GIP_R2DENT(".xml", true),
    GIP_R2DEPI(".xml", false),
    GIP_R2EOB2(".xml", true),
    GIP_R2EQOG(".xml", true),
    GIP_R2L2NC(".xml", true),
    GIP_R2MACO(".xml", true),
    GIP_R2NOMO(".xml", false),
    GIP_R2PARA(".xml", false),
    GIP_R2SWIR(".xml", false),
    GIP_R2WAFI(".xml", true),
    GIP_RESPAR(".xml", false),
    GIP_SPAMOD(".xml", false),
    GIP_TILPAR(".xml", false),
    GIP_VIEDIR(".xml", true);

    private final String extension;
    private final boolean bandDependent;

}
